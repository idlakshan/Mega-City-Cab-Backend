package com.mcc.backend.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CrudDAO<T, ID> {
    T findById(Connection connection, ID id) throws SQLException;
    List<T> findAll(Connection connection) throws SQLException;
    boolean save(Connection connection, T entity) throws SQLException;
    boolean update(Connection connection, T entity) throws SQLException;
    boolean delete(Connection connection, ID id) throws SQLException;
}
