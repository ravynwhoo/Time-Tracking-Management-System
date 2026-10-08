package com.timetrack.dao;

import com.timetrack.exception.DatabaseException;
import com.timetrack.model.ProductivitySummary;
import com.timetrack.model.TimeLog;
import com.timetrack.util.DBUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of TimeLogDAO.
 * Supports CRUD, dynamic filtering, joins with users, and aggregation queries for productivity metrics.
 */
public class TimeLogDAOImpl implements TimeLogDAO {

    private static final String BASE_SELECT =
            "SELECT t.id, t.user_id, u.name AS user_name, t.log_date, t.start_time, t.end_time, t.task, t.duration, t.created_at " +
            "FROM time_logs t " +
            "INNER JOIN users u ON t.user_id = u.id ";

    @Override
    public TimeLog findById(Integer id) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE t.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding time log by id: " + id, e);
        }
    }

    @Override
    public List<TimeLog> findAll() throws DatabaseException {
        String sql = BASE_SELECT + "ORDER BY t.log_date DESC, t.start_time DESC";
        List<TimeLog> logs = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(mapRow(rs));
            }
            return logs;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all time logs", e);
        }
    }

    @Override
    public List<TimeLog> findByUserId(int userId) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE t.user_id = ? ORDER BY t.log_date DESC, t.start_time DESC";
        List<TimeLog> logs = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
            return logs;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching logs for user: " + userId, e);
        }
    }

    @Override
    public List<TimeLog> findByDateRange(Date startDate, Date endDate) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE t.log_date BETWEEN ? AND ? ORDER BY t.log_date DESC, t.start_time DESC";
        List<TimeLog> logs = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
            return logs;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching logs between " + startDate + " and " + endDate, e);
        }
    }

    @Override
    public List<TimeLog> findByUserIdAndDateRange(int userId, Date startDate, Date endDate) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE t.user_id = ? AND t.log_date BETWEEN ? AND ? ORDER BY t.log_date DESC, t.start_time DESC";
        List<TimeLog> logs = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
            return logs;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching user logs by date range", e);
        }
    }

    @Override
    public List<TimeLog> searchLogs(Integer userId, String keyword, Date startDate, Date endDate) throws DatabaseException {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append("AND t.user_id = ? ");
            params.add(userId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(t.task) LIKE ? OR LOWER(u.name) LIKE ?) ");
            String term = "%" + keyword.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
        }
        if (startDate != null) {
            sql.append("AND t.log_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("AND t.log_date <= ? ");
            params.add(endDate);
        }
        sql.append("ORDER BY t.log_date DESC, t.start_time DESC");

        List<TimeLog> logs = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else if (p instanceof Date) {
                    ps.setDate(i + 1, (Date) p);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
            return logs;
        } catch (SQLException e) {
            throw new DatabaseException("Error searching time logs", e);
        }
    }

    @Override
    public ProductivitySummary getProductivitySummary(Integer userId, Date startDate, Date endDate) throws DatabaseException {
        ProductivitySummary summary = new ProductivitySummary();

        // 1. Overall stats: SUM(duration), COUNT(*), COUNT(DISTINCT log_date)
        StringBuilder statsSql = new StringBuilder("SELECT COALESCE(SUM(duration), 0) AS total_hours, ")
                .append("COUNT(*) AS total_entries, ")
                .append("COUNT(DISTINCT log_date) AS active_days ")
                .append("FROM time_logs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (userId != null) {
            statsSql.append("AND user_id = ? ");
            params.add(userId);
        }
        if (startDate != null) {
            statsSql.append("AND log_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            statsSql.append("AND log_date <= ? ");
            params.add(endDate);
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(statsSql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalHours = rs.getBigDecimal("total_hours");
                    int totalEntries = rs.getInt("total_entries");
                    int activeDays = rs.getInt("active_days");

                    summary.setTotalHours(totalHours);
                    summary.setTotalEntries(totalEntries);
                    summary.setUniqueActiveDays(activeDays);

                    if (activeDays > 0) {
                        BigDecimal avg = totalHours.divide(BigDecimal.valueOf(activeDays), 2, RoundingMode.HALF_UP);
                        summary.setAverageHoursPerDay(avg);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error computing productivity summary stats", e);
        }

        // 2. Daily breakdown
        StringBuilder dailySql = new StringBuilder("SELECT log_date, SUM(duration) AS hours ")
                .append("FROM time_logs WHERE 1=1 ");
        if (userId != null) dailySql.append("AND user_id = ? ");
        if (startDate != null) dailySql.append("AND log_date >= ? ");
        if (endDate != null) dailySql.append("AND log_date <= ? ");
        dailySql.append("GROUP BY log_date ORDER BY log_date ASC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(dailySql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, BigDecimal> daily = new LinkedHashMap<>();
                while (rs.next()) {
                    daily.put(rs.getDate("log_date").toString(), rs.getBigDecimal("hours"));
                }
                summary.setDailyHours(daily);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error computing daily hours breakdown", e);
        }

        // 3. Task breakdown
        StringBuilder taskSql = new StringBuilder("SELECT task, SUM(duration) AS hours ")
                .append("FROM time_logs WHERE 1=1 ");
        if (userId != null) taskSql.append("AND user_id = ? ");
        if (startDate != null) taskSql.append("AND log_date >= ? ");
        if (endDate != null) taskSql.append("AND log_date <= ? ");
        taskSql.append("GROUP BY task ORDER BY hours DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(taskSql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, BigDecimal> tasks = new LinkedHashMap<>();
                boolean first = true;
                while (rs.next()) {
                    String task = rs.getString("task");
                    BigDecimal hours = rs.getBigDecimal("hours");
                    tasks.put(task, hours);
                    if (first) {
                        summary.setTopTask(task);
                        summary.setTopTaskHours(hours);
                        first = false;
                    }
                }
                summary.setTaskHours(tasks);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error computing task hours breakdown", e);
        }

        // 4. User breakdown (for Admin/team reporting)
        StringBuilder userSql = new StringBuilder("SELECT u.name, SUM(t.duration) AS hours ")
                .append("FROM time_logs t JOIN users u ON t.user_id = u.id WHERE 1=1 ");
        if (userId != null) userSql.append("AND t.user_id = ? ");
        if (startDate != null) userSql.append("AND t.log_date >= ? ");
        if (endDate != null) userSql.append("AND t.log_date <= ? ");
        userSql.append("GROUP BY u.id, u.name ORDER BY hours DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(userSql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, BigDecimal> users = new LinkedHashMap<>();
                while (rs.next()) {
                    users.put(rs.getString("name"), rs.getBigDecimal("hours"));
                }
                summary.setUserHours(users);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error computing user hours breakdown", e);
        }

        return summary;
    }

    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p instanceof Integer) {
                ps.setInt(i + 1, (Integer) p);
            } else if (p instanceof Date) {
                ps.setDate(i + 1, (Date) p);
            } else if (p instanceof String) {
                ps.setString(i + 1, (String) p);
            }
        }
    }

    @Override
    public TimeLog save(TimeLog log) throws DatabaseException {
        String sql = "INSERT INTO time_logs (user_id, log_date, start_time, end_time, task, duration) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getUserId());
            ps.setDate(2, log.getLogDate());
            ps.setTime(3, log.getStartTime());
            ps.setTime(4, log.getEndTime());
            ps.setString(5, log.getTask());
            ps.setBigDecimal(6, log.getDuration());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating time log failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getInt(1));
                }
            }
            return log;
        } catch (SQLException e) {
            throw new DatabaseException("Error saving time log", e);
        }
    }

    @Override
    public boolean update(TimeLog log) throws DatabaseException {
        String sql = "UPDATE time_logs SET user_id = ?, log_date = ?, start_time = ?, end_time = ?, task = ?, duration = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, log.getUserId());
            ps.setDate(2, log.getLogDate());
            ps.setTime(3, log.getStartTime());
            ps.setTime(4, log.getEndTime());
            ps.setString(5, log.getTask());
            ps.setBigDecimal(6, log.getDuration());
            ps.setInt(7, log.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating time log with id: " + log.getId(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        String sql = "DELETE FROM time_logs WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting time log with id: " + id, e);
        }
    }

    private TimeLog mapRow(ResultSet rs) throws SQLException {
        TimeLog log = new TimeLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setUserName(rs.getString("user_name"));
        log.setLogDate(rs.getDate("log_date"));
        log.setStartTime(rs.getTime("start_time"));
        log.setEndTime(rs.getTime("end_time"));
        log.setTask(rs.getString("task"));
        log.setDuration(rs.getBigDecimal("duration"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
