package com.timetrack.dao;

import com.timetrack.exception.DatabaseException;
import com.timetrack.model.User;

import java.util.List;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDAO extends GenericDAO<User, Integer> {

    /**
     * Finds a user by email address.
     */
    User findByEmail(String email) throws DatabaseException;

    /**
     * Checks if an email is already registered.
     */
    boolean emailExists(String email) throws DatabaseException;

    /**
     * Retrieves users filtered by role.
     */
    List<User> findByRole(String role) throws DatabaseException;

    /**
     * Counts total registered users.
     */
    int countUsers() throws DatabaseException;
}
