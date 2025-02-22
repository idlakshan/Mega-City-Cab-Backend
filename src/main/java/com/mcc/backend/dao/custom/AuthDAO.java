package com.mcc.backend.dao.custom;

import com.mcc.backend.dao.CrudDAO;
import com.mcc.backend.entity.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface AuthDAO extends CrudDAO<User, Integer> {
    int findUserIdByEmailAndPassword(Connection connection, String email, String password) throws SQLException;
    String findRoleByUserId(Connection connection, int userId) throws SQLException;
    int saveUser(Connection connection, User user) throws SQLException;
    void saveUserDetails(Connection connection, int userId, int roleId) throws SQLException;
    int findRoleIdByName(Connection connection, String roleName) throws SQLException;
    void deleteUserDetails(Connection connection, int userId) throws SQLException;
    void deletePaymentsByUserId(Connection connection, int userId) throws SQLException;
    void deleteBookingsByUserId(Connection connection, int userId) throws SQLException;
}