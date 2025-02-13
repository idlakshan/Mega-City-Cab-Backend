package com.mcc.backend.dao.custom.impl;


import com.mcc.backend.dao.custom.CategoryDAO;
import com.mcc.backend.entity.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {
    @Override
    public List<Category> getAllCategories(Connection connection) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM category";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("id"));
                category.setName(resultSet.getString("name"));
                category.setIcon(resultSet.getString("icon"));
                category.setTitle(resultSet.getString("title"));
                category.setFeatures(resultSet.getString("features"));
                category.setPrice(resultSet.getDouble("price"));
                categories.add(category);
            }
        }

        return categories;
    }

    @Override
    public List<Category> getAllCategoryNames(Connection connection) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT id,name FROM category";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("id"));
                category.setName(resultSet.getString("name"));
                categories.add(category);
            }
        }

        return categories;
    }
}