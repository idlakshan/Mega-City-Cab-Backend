package com.mcc.backend.dao.custom;

import com.mcc.backend.entity.Category;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CategoryDAO {
    List<Category> getAllCategories(Connection connection) throws SQLException;
    List<Category> getAllCategoryNames(Connection connection) throws SQLException;
    boolean updateCategoryPrice(Connection connection, int id, double newPrice) throws SQLException;
}