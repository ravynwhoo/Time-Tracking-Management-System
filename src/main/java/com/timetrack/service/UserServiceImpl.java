package com.timetrack.service;

import com.timetrack.dao.UserDAO;
import com.timetrack.dao.UserDAOImpl;
import com.timetrack.exception.AuthenticationException;
import com.timetrack.exception.DatabaseException;
import com.timetrack.exception.ResourceNotFoundException;
import com.timetrack.exception.ValidationException;
import com.timetrack.model.User;
import com.timetrack.util.PasswordUtil;

import java.util.List;

/**
 * Service implementation for User management and authentication.
 */
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User authenticate(String email, String password) throws AuthenticationException, DatabaseException {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Email and password are required.");
        }

        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new AuthenticationException("Invalid email or password.");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return user;
    }

    @Override
    public User register(User user) throws ValidationException, DatabaseException {
        validateUser(user, true);

        if (userDAO.emailExists(user.getEmail().trim().toLowerCase())) {
            throw new ValidationException("An account with email '" + user.getEmail() + "' already exists.");
        }

        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setName(user.getName().trim());
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole(User.ROLE_USER);
        }

        return userDAO.save(user);
    }

    @Override
    public User updateUser(User user) throws ValidationException, ResourceNotFoundException, DatabaseException {
        if (user.getId() == null) {
            throw new ValidationException("User ID is required for update.");
        }

        User existing = userDAO.findById(user.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("User not found with ID: " + user.getId());
        }

        validateUser(user, false);

        // Check if updating email to another existing user's email
        User emailOwner = userDAO.findByEmail(user.getEmail().trim().toLowerCase());
        if (emailOwner != null && !emailOwner.getId().equals(user.getId())) {
            throw new ValidationException("Email '" + user.getEmail() + "' is already in use by another account.");
        }

        existing.setName(user.getName().trim());
        existing.setEmail(user.getEmail().trim().toLowerCase());
        if (user.getRole() != null) {
            existing.setRole(user.getRole());
        }

        // If a new password is provided, hash and update it
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existing.setPassword(PasswordUtil.hashPassword(user.getPassword().trim()));
        }

        boolean ok = userDAO.update(existing);
        if (!ok) {
            throw new DatabaseException("Failed to update user ID: " + user.getId());
        }
        return existing;
    }

    @Override
    public void deleteUser(int id) throws ResourceNotFoundException, DatabaseException {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        boolean ok = userDAO.delete(id);
        if (!ok) {
            throw new DatabaseException("Failed to delete user ID: " + id);
        }
    }

    @Override
    public User getUserById(int id) throws ResourceNotFoundException, DatabaseException {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) throws DatabaseException {
        if (email == null) return null;
        return userDAO.findByEmail(email.trim().toLowerCase());
    }

    @Override
    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.findAll();
    }

    @Override
    public List<User> getStandardUsers() throws DatabaseException {
        return userDAO.findByRole(User.ROLE_USER);
    }

    @Override
    public int getUserCount() throws DatabaseException {
        return userDAO.countUsers();
    }

    private void validateUser(User user, boolean requirePassword) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User details cannot be empty.");
        }
        if (user.getName() == null || user.getName().trim().length() < 2) {
            throw new ValidationException("Full name must be at least 2 characters.");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("A valid email address is required.");
        }
        if (requirePassword) {
            if (user.getPassword() == null || user.getPassword().trim().length() < 6) {
                throw new ValidationException("Password must be at least 6 characters.");
            }
        }
    }
}
