package com.timetrack.service;

import com.timetrack.dao.SystemSettingDAO;
import com.timetrack.dao.SystemSettingDAOImpl;
import com.timetrack.exception.DatabaseException;

import java.util.Map;

/**
 * Service for system configurations.
 */
public class SettingsService {

    private final SystemSettingDAO settingDAO;

    public SettingsService() {
        this.settingDAO = new SystemSettingDAOImpl();
    }

    public SettingsService(SystemSettingDAO settingDAO) {
        this.settingDAO = settingDAO;
    }

    public Map<String, String> getAllSettings() throws DatabaseException {
        return settingDAO.getAllSettings();
    }

    public String getSetting(String key, String defaultValue) throws DatabaseException {
        return settingDAO.getSetting(key, defaultValue);
    }

    public void updateSetting(String key, String value) throws DatabaseException {
        settingDAO.saveOrUpdate(key, value);
    }

    public void updateSettings(Map<String, String> settings) throws DatabaseException {
        if (settings != null) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                settingDAO.saveOrUpdate(entry.getKey(), entry.getValue());
            }
        }
    }
}
