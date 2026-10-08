package com.timetrack.dao;

import com.timetrack.exception.DatabaseException;
import com.timetrack.model.ProductivitySummary;
import com.timetrack.model.TimeLog;

import java.sql.Date;
import java.util.List;

/**
 * Data Access Object interface for TimeLog entries.
 */
public interface TimeLogDAO extends GenericDAO<TimeLog, Integer> {

    /**
     * Retrieves all time logs for a specific user.
     */
    List<TimeLog> findByUserId(int userId) throws DatabaseException;

    /**
     * Retrieves time logs within a date range (across all users).
     */
    List<TimeLog> findByDateRange(Date startDate, Date endDate) throws DatabaseException;

    /**
     * Retrieves time logs for a specific user within a date range.
     */
    List<TimeLog> findByUserIdAndDateRange(int userId, Date startDate, Date endDate) throws DatabaseException;

    /**
     * Searches time logs with optional user filter, task keyword, and date range.
     */
    List<TimeLog> searchLogs(Integer userId, String keyword, Date startDate, Date endDate) throws DatabaseException;

    /**
     * Calculates productivity metrics (total hours, daily breakdown, task breakdown)
     * for a given user (or all users if userId is null) and date range.
     */
    ProductivitySummary getProductivitySummary(Integer userId, Date startDate, Date endDate) throws DatabaseException;
}
