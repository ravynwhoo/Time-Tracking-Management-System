package com.timetrack.service;

import com.timetrack.dao.TimeLogDAO;
import com.timetrack.dao.TimeLogDAOImpl;
import com.timetrack.exception.DatabaseException;
import com.timetrack.exception.ResourceNotFoundException;
import com.timetrack.exception.ValidationException;
import com.timetrack.model.ProductivitySummary;
import com.timetrack.model.TimeLog;
import com.timetrack.model.User;
import com.timetrack.util.DateUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Service implementation for Time Logs and Productivity business logic.
 */
public class TimeLogServiceImpl implements TimeLogService {

    private final TimeLogDAO timeLogDAO;

    public TimeLogServiceImpl() {
        this.timeLogDAO = new TimeLogDAOImpl();
    }

    public TimeLogServiceImpl(TimeLogDAO timeLogDAO) {
        this.timeLogDAO = timeLogDAO;
    }

    @Override
    public TimeLog createLog(TimeLog log) throws ValidationException, DatabaseException {
        validateLog(log);

        // Auto calculate duration if missing or ensure calculation accuracy
        BigDecimal computedDuration = DateUtil.calculateDuration(log.getStartTime(), log.getEndTime());
        if (computedDuration.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("End time must be after start time.");
        }
        log.setDuration(computedDuration);

        return timeLogDAO.save(log);
    }

    @Override
    public TimeLog updateLog(TimeLog log, User currentUser) throws ValidationException, ResourceNotFoundException, DatabaseException {
        if (log.getId() == null) {
            throw new ValidationException("Log ID is required for update.");
        }

        TimeLog existing = timeLogDAO.findById(log.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("Time log not found with ID: " + log.getId());
        }

        // Authorization check: User can only update their own log unless Admin
        if (!currentUser.isAdmin() && !existing.getUserId().equals(currentUser.getId())) {
            throw new ValidationException("You are not authorized to edit this time log.");
        }

        validateLog(log);

        BigDecimal computedDuration = DateUtil.calculateDuration(log.getStartTime(), log.getEndTime());
        if (computedDuration.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("End time must be after start time.");
        }

        existing.setLogDate(log.getLogDate());
        existing.setStartTime(log.getStartTime());
        existing.setEndTime(log.getEndTime());
        existing.setTask(log.getTask().trim());
        existing.setDuration(computedDuration);
        if (currentUser.isAdmin() && log.getUserId() != null) {
            existing.setUserId(log.getUserId());
        }

        boolean ok = timeLogDAO.update(existing);
        if (!ok) {
            throw new DatabaseException("Failed to update time log ID: " + log.getId());
        }
        return existing;
    }

    @Override
    public void deleteLog(int logId, User currentUser) throws ResourceNotFoundException, DatabaseException {
        TimeLog existing = timeLogDAO.findById(logId);
        if (existing == null) {
            throw new ResourceNotFoundException("Time log not found with ID: " + logId);
        }

        // Authorization check: User can only delete their own log unless Admin
        if (!currentUser.isAdmin() && !existing.getUserId().equals(currentUser.getId())) {
            throw new DatabaseException("You are not authorized to delete this time log.");
        }

        boolean ok = timeLogDAO.delete(logId);
        if (!ok) {
            throw new DatabaseException("Failed to delete time log ID: " + logId);
        }
    }

    @Override
    public TimeLog getLogById(int id) throws ResourceNotFoundException, DatabaseException {
        TimeLog log = timeLogDAO.findById(id);
        if (log == null) {
            throw new ResourceNotFoundException("Time log not found with ID: " + id);
        }
        return log;
    }

    @Override
    public List<TimeLog> getUserLogs(int userId) throws DatabaseException {
        return timeLogDAO.findByUserId(userId);
    }

    @Override
    public List<TimeLog> getAllLogs() throws DatabaseException {
        return timeLogDAO.findAll();
    }

    @Override
    public List<TimeLog> searchLogs(Integer userId, String keyword, Date startDate, Date endDate) throws DatabaseException {
        return timeLogDAO.searchLogs(userId, keyword, startDate, endDate);
    }

    @Override
    public ProductivitySummary getProductivitySummary(Integer userId, Date startDate, Date endDate) throws DatabaseException {
        return timeLogDAO.getProductivitySummary(userId, startDate, endDate);
    }

    private void validateLog(TimeLog log) throws ValidationException {
        if (log == null) {
            throw new ValidationException("Time log data is missing.");
        }
        if (log.getUserId() == null) {
            throw new ValidationException("User ID is required.");
        }
        if (log.getLogDate() == null) {
            throw new ValidationException("Log date is required.");
        }
        if (log.getStartTime() == null) {
            throw new ValidationException("Start time is required.");
        }
        if (log.getEndTime() == null) {
            throw new ValidationException("End time is required.");
        }
        if (log.getTask() == null || log.getTask().trim().isEmpty()) {
            throw new ValidationException("Task description cannot be empty.");
        }
        if (log.getTask().trim().length() > 255) {
            throw new ValidationException("Task description must not exceed 255 characters.");
        }
    }
}
