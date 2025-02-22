package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.UserDTO;

import java.sql.SQLException;
import java.util.List;

public interface AuthBO {
    String login(String email, String password) throws SQLException, ClassNotFoundException;
    boolean signUp(UserDTO user) throws SQLException, ClassNotFoundException;
    UserDTO getUserById(int userId) throws SQLException, ClassNotFoundException;
    List<UserDTO> getAllUsers() throws SQLException, ClassNotFoundException;
    boolean deleteUser(int userId) throws SQLException, ClassNotFoundException;
}