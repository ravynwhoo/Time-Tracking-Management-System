package com.timetrack.service;

import com.timetrack.exception.DatabaseException;
import com.timetrack.exception.ResourceNotFoundException;
import com.timetrack.exception.ValidationException;
import com.timetrack.model.ProductivitySummary;
import com.timetrack.model.TimeLog;
import com.timetrack.model.User;

import java.sql.Date;
import java.util.List;

/**
 * Service interface for Time Logging and Productivity tracking.
 */
public interface TimeLogService {

    /**
     * Creates a new time log with automated duration calculation and validation.
     */
    TimeLog createLog(TimeLog log) throws ValidationException, DatabaseException;

    /**
     * Updates an existing time log with ownership/admin verification.
     */
    TimeLog updateLog(TimeLog log, User currentUser) throws ValidationException, ResourceNotFoundException, DatabaseException;

    /**
     * Deletes a time log with ownership/admin verification.
     */
    void deleteLog(int logId, User currentUser) throws ResourceNotFoundException, DatabaseException;

    /**
     * Retrieves log by ID.
     */
    TimeLog getLogById(int id) throws ResourceNotFoundException, DatabaseException;

    /**
     * Retrieves all logs for a user.
     */
    List<TimeLog> getUserLogs(int userId) throws DatabaseException;

    /**
     * Retrieves all logs across all users (for admin).
     */
    List<TimeLog> getAllLogs() throws DatabaseException;

    /**
     * Searches logs by criteria.
     */
    List<TimeLog> searchLogs(Integer userId, String keyword, Date startDate, Date endDate) throws DatabaseException;

    /**
     * Computes productivity summary for reporting and charts.
     */
    ProductivitySummary getProductivitySummary(Integer userId, Date startDate, Date endDate) throws DatabaseException;
}
