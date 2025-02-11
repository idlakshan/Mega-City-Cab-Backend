package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.AuthBO;
import com.mcc.backend.config.Security;
import com.mcc.backend.dao.custom.AuthDAO;
import com.mcc.backend.dao.custom.impl.AuthDAOImpl;
import com.mcc.backend.dto.UserDTO;
import com.mcc.backend.entity.User;
import com.mcc.backend.servlet.AuthServlet;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthBOImpl implements AuthBO {

    private final AuthDAO authDAO = new AuthDAOImpl();

    @Override
    public String login(String email, String password) throws SQLException {
        Connection connection = null;
        try {
            connection = AuthServlet.dataSource.getConnection();
            connection.setAutoCommit(false);

            int userId = authDAO.findUserIdByEmailAndPassword(connection, email, password);
            System.out.println(userId);
            if (userId == -1) {
                connection.rollback();
                return null;
            }

            String role = authDAO.findRoleByUserId(connection, userId);
            System.out.println(role);
            if (role == null) {
                connection.rollback();
                return null;
            }

            connection.commit();
            String token = Security.createJWT(userId, role);

            System.out.println("Generated Token in AuthBOImpl: " + token);
            System.out.println(userId+" "+role);// Debug statement
            return token;
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    @Override
    public boolean signUp(UserDTO userDTO) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = AuthServlet.dataSource.getConnection();
            connection.setAutoCommit(false);


            User user = new User();
            user.setName(userDTO.getName());
            user.setNic(userDTO.getNic());
            user.setPhone(userDTO.getPhone());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());

            int userId = authDAO.saveUser(connection, user);
            if (userId == -1) {
                connection.rollback();
                return false;
            }

            int roleId = authDAO.findRoleIdByName(connection, "CUSTOMER");
            if (roleId == -1) {
                connection.rollback();
                return false;
            }

            authDAO.saveUserDetails(connection, userId, roleId);
            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    @Override
    public UserDTO getUserById(int userId) throws SQLException {
        Connection connection = null;
        try {
            connection = AuthServlet.dataSource.getConnection();
            User user = authDAO.findUserById(connection, userId);
            UserDTO userDTO = new UserDTO();
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            return userDTO;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}