package com.timetrack.dao;

import com.timetrack.exception.DatabaseException;
import com.timetrack.model.SystemSetting;

import java.util.Map;

/**
 * Data Access Object interface for System Configuration Settings.
 */
public interface SystemSettingDAO {

    /**
     * Gets a setting value by key, returning defaultValue if not found.
     */
    String getSetting(String key, String defaultValue) throws DatabaseException;

    /**
     * Saves or updates a setting key-value pair.
     */
    void saveOrUpdate(String key, String value) throws DatabaseException;

    /**
     * Gets all settings as a key-value Map.
     */
    Map<String, String> getAllSettings() throws DatabaseException;
}
