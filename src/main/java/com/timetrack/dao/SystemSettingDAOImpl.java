package com.timetrack.dao;

import com.timetrack.exception.DatabaseException;
import com.timetrack.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JDBC implementation of SystemSettingDAO.
 */
public class SystemSettingDAOImpl implements SystemSettingDAO {

    @Override
    public String getSetting(String key, String defaultValue) throws DatabaseException {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
            return defaultValue;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving system setting: " + key, e);
        }
    }

    @Override
    public void saveOrUpdate(String key, String value) throws DatabaseException {
        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error saving system setting: " + key, e);
        }
    }

    @Override
    public Map<String, String> getAllSettings() throws DatabaseException {
        String sql = "SELECT setting_key, setting_value FROM system_settings ORDER BY setting_key ASC";
        Map<String, String> map = new LinkedHashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
            return map;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all settings", e);
        }
    }
}
