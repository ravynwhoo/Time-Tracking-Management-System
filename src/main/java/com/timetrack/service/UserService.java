package com.timetrack.service;

import com.timetrack.exception.AuthenticationException;
import com.timetrack.exception.DatabaseException;
import com.timetrack.exception.ResourceNotFoundException;
import com.timetrack.exception.ValidationException;
import com.timetrack.model.User;

import java.util.List;

/**
 * Service interface for User business operations.
 */
public interface UserService {

    /**
     * Authenticates a user with email and password.
     */
    User authenticate(String email, String password) throws AuthenticationException, DatabaseException;

    /**
     * Registers a new user.
     */
    User register(User user) throws ValidationException, DatabaseException;

    /**
     * Updates an existing user's profile or credentials.
     */
    User updateUser(User user) throws ValidationException, ResourceNotFoundException, DatabaseException;

    /**
     * Deletes a user by identifier.
     */
    void deleteUser(int id) throws ResourceNotFoundException, DatabaseException;

    /**
     * Finds user by ID.
     */
    User getUserById(int id) throws ResourceNotFoundException, DatabaseException;

    /**
     * Finds user by email.
     */
    User getUserByEmail(String email) throws DatabaseException;

    /**
     * Retrieves all users.
     */
    List<User> getAllUsers() throws DatabaseException;

    /**
     * Retrieves users with the role 'user'.
     */
    List<User> getStandardUsers() throws DatabaseException;

    /**
     * Returns total count of registered users.
     */
    int getUserCount() throws DatabaseException;
}
