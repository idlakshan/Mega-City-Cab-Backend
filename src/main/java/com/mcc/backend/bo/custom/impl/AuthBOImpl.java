package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.AuthBO;
import com.mcc.backend.config.Security;
import com.mcc.backend.dao.custom.AuthDAO;
import com.mcc.backend.dao.custom.impl.AuthDAOImpl;
import com.mcc.backend.dto.RoleDTO;
import com.mcc.backend.dto.UserDTO;
import com.mcc.backend.entity.User;
import com.mcc.backend.servlet.AuthServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthBOImpl implements AuthBO {

    private final AuthDAO authDAO = new AuthDAOImpl();

    @Override
    public String login(String email, String password) throws SQLException {
        try (Connection connection = AuthServlet.dataSource.getConnection()) {
            int userId = authDAO.findUserIdByEmailAndPassword(connection, email, password);
            if (userId == -1) {
                throw new SQLException("Invalid email or password");
            }

            String role = authDAO.findRoleByUserId(connection, userId);
            if (role == null) {
                throw new SQLException("Role not found for user ID: " + userId);
            }

            return Security.createJWT(userId, role);
        }
    }


    @Override
    public boolean signUp(UserDTO userDTO) throws SQLException {
        try (Connection connection = AuthServlet.dataSource.getConnection()) {
            connection.setAutoCommit(false);

            User user = new User();
            user.setName(userDTO.getName());
            user.setNic(userDTO.getNic());
            user.setPhone(userDTO.getPhone());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());

            int userId = authDAO.saveUser(connection, user);
            if (userId == -1) {
                throw new SQLException("Failed to save user");
            }

            int roleId = authDAO.findRoleIdByName(connection, "CUSTOMER");
            if (roleId == -1) {
                throw new SQLException("Role 'CUSTOMER' not found");
            }

            authDAO.saveUserDetails(connection, userId, roleId);
            connection.commit();
            return true;
        } catch (SQLException e) {
            throw new SQLException("Sign-up failed: " + e.getMessage(), e);
        }
    }


    @Override
    public UserDTO getUserById(int userId) throws SQLException {
        try (Connection connection = AuthServlet.dataSource.getConnection()) {
            User user = authDAO.findById(connection, userId);
            if (user == null) {
                return null;
            }
            UserDTO userDTO = new UserDTO();
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            return userDTO;
        }
    }


    @Override
    public List<UserDTO> getAllUsers() throws SQLException, ClassNotFoundException {
        try (Connection conn = AuthServlet.dataSource.getConnection()) {
            List<User> allUsers = authDAO.findAll(conn);
            List<UserDTO> userDTOList = new ArrayList<>();

            for (User user : allUsers) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setName(user.getName());
                userDTO.setNic(user.getNic());
                userDTO.setPhone(user.getPhone());
                userDTO.setEmail(user.getEmail());

                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(user.getRole().getId());
                roleDTO.setName(user.getRole().getName());
                userDTO.setRole(roleDTO);

                userDTOList.add(userDTO);
            }
            return userDTOList;
        }
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException {
        try (Connection connection = AuthServlet.dataSource.getConnection()) {
            connection.setAutoCommit(false);

            authDAO.deleteUserDetails(connection, userId);
            authDAO.deletePaymentsByUserId(connection, userId);
            authDAO.deleteBookingsByUserId(connection, userId);
            boolean isUserDeleted = authDAO.delete(connection, userId);

            connection.commit();
            return isUserDeleted;
        } catch (SQLException e) {
            throw new SQLException("Failed to delete user with ID: " + userId, e);
        }
    }

}