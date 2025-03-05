package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.AuthDAO;
import com.mcc.backend.entity.Role;
import com.mcc.backend.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthDAOImpl implements AuthDAO {

    @Override
    public int findUserIdByEmailAndPassword(Connection connection, String email, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM user WHERE email = ? AND password = ?")) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("id") : -1;
            }
        }
    }

    @Override
    public String findRoleByUserId(Connection connection, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT r.name FROM role r JOIN userdetails ud ON r.id = ud.role_id WHERE ud.user_Id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("name") : null;
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
                return generatedKeys.next() ? generatedKeys.getInt(1) : -1;
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
                return resultSet.next() ? resultSet.getInt("id") : -1;
            }
        }
    }

    @Override
    public void deleteUserDetails(Connection connection, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM userdetails WHERE user_id = ?")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deletePaymentsByUserId(Connection connection, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM payment WHERE booking_id IN (SELECT booking_id FROM booking WHERE user_id = ?)")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteBookingsByUserId(Connection connection, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM booking WHERE user_id = ?")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public User findById(Connection connection, Integer userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT name, email, phone FROM user WHERE id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPhone(resultSet.getString("phone"));
                    return user;
                }
                return null;
            }
        }
    }

    @Override
    public List<User> findAll(Connection connection) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.phone, u.nic, u.email, r.id AS role_id, r.name AS role " +
                "FROM user u " +
                "LEFT JOIN userdetails ud ON u.id = ud.user_id " +
                "LEFT JOIN role r ON ud.role_id = r.id";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setNic(rs.getString("nic"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));

                Role role = new Role();
                role.setId(rs.getInt("role_id"));
                role.setName(rs.getString("role"));
                user.setRole(role);

                users.add(user);
            }
        }
        return users;
    }

    @Override
    public boolean save(Connection connection, User entity) throws SQLException {
        return false;
    }

    @Override
    public boolean update(Connection connection, User user) throws SQLException {
        String sql = "UPDATE user SET name = ?, email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setInt(4, user.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    @Override
    public boolean delete(Connection connection, Integer userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM user WHERE id = ?")) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        }
    }
}