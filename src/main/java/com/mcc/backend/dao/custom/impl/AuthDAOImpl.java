package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.AuthDAO;
import com.mcc.backend.dto.UserDTO;
import com.mcc.backend.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAOImpl implements AuthDAO {

    @Override
    public int findUserIdByEmailAndPassword(Connection connection, String email, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM user WHERE email = ? AND password = ?")) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
                return -1;
            }
        }
    }

    @Override
    public String findRoleByUserId(Connection connection, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT r.name FROM role r JOIN userdetails ud ON r.id = ud.role_id WHERE ud.user_Id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
                return null;
            }
        }
    }


    @Override
    public int saveUser(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user (name, nic, phone, email, password) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getNic());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPassword());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                return -1;
            }
        }
    }

    @Override
    public void saveUserDetails(Connection connection, int userId, int roleId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO userdetails (user_Id, role_id) VALUES (?, ?)")) {
            statement.setInt(1, userId);
            statement.setInt(2, roleId);
            statement.executeUpdate();
        }
    }

    @Override
    public int findRoleIdByName(Connection connection, String roleName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM role WHERE name = ?")) {
            statement.setString(1, roleName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
                return -1;
            }
        }
    }
}