package com.timetrack.dao;

import com.timetrack.exception.DatabaseException;
import java.util.List;

/**
 * Generic Data Access Object interface showcasing Generic types and standard CRUD operations.
 *
 * @param <T>  the entity type
 * @param <ID> the primary key identifier type
 */
public interface GenericDAO<T, ID> {

    /**
     * Finds an entity by its primary key.
     * @param id identifier
     * @return the entity, or null if not found
     * @throws DatabaseException on database errors
     */
    T findById(ID id) throws DatabaseException;

    /**
     * Retrieves all entities.
     * @return List of entities
     * @throws DatabaseException on database errors
     */
    List<T> findAll() throws DatabaseException;

    /**
     * Persists a new entity.
     * @param entity the entity to create
     * @return the persisted entity with generated ID populated
     * @throws DatabaseException on database errors
     */
    T save(T entity) throws DatabaseException;

    /**
     * Updates an existing entity.
     * @param entity the entity to update
     * @return true if updated, false otherwise
     * @throws DatabaseException on database errors
     */
    boolean update(T entity) throws DatabaseException;

    /**
     * Deletes an entity by primary key.
     * @param id identifier
     * @return true if deleted, false otherwise
     * @throws DatabaseException on database errors
     */
    boolean delete(ID id) throws DatabaseException;
}
