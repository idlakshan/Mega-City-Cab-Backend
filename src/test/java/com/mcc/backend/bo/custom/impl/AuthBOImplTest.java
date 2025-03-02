package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.dao.custom.AuthDAO;
import com.mcc.backend.dto.RoleDTO;
import com.mcc.backend.dto.UserDTO;
import com.mcc.backend.entity.Role;
import com.mcc.backend.entity.User;
import com.mcc.backend.servlet.AuthServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthBOImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private AuthDAO authDAO;

    private AuthBOImpl authBO;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        authBO = new AuthBOImpl();
        AuthServlet.dataSource = dataSource;
    }

    @Test
    public void testLoginSuccess() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("ADMIN");

        String token = authBO.login("admin@gmail.com", "admin123");
        assertNotNull(token);
    }

    @Test
    public void testLoginFailure() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertThrows(SQLException.class, () -> authBO.login("wrong@example.com", "wrongpassword"));
    }

    @Test
    public void testSignUpSuccess() throws SQLException {

        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(3);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(2);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("New User");
        userDTO.setNic("123456789V");
        userDTO.setPhone("0712345678");
        userDTO.setEmail("newuser@gmail.com");
        userDTO.setPassword("password123");

        boolean isSignedUp = authBO.signUp(userDTO);
        assertTrue(isSignedUp);
    }

    @Test
    public void testSignUpFailure() throws SQLException {
        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);


        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setNic("123456789V");
        userDTO.setPhone("1234567890");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");

        assertThrows(SQLException.class, () -> authBO.signUp(userDTO));
    }

    @Test
    public void testGetUserById() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("Dimuthu Lakshan");
        when(resultSet.getString("email")).thenReturn("admin@gmail.com");
        when(resultSet.getString("phone")).thenReturn("0714038546");

        UserDTO userDTO = authBO.getUserById(1);
        assertNotNull(userDTO);
        assertEquals("Dimuthu Lakshan", userDTO.getName());
        assertEquals("admin@gmail.com", userDTO.getEmail());
        assertEquals("0714038546", userDTO.getPhone());
    }

    @Test
    public void testGetAllUsers() throws SQLException, ClassNotFoundException {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false); // Two rows
        when(resultSet.getInt("id")).thenReturn(1, 2);
        when(resultSet.getString("name")).thenReturn("Dimuthu Lakshan", "Kasun Rajitha");
        when(resultSet.getString("nic")).thenReturn("970820123V", "987654321V");
        when(resultSet.getString("phone")).thenReturn("0714038546", "0779876543");
        when(resultSet.getString("email")).thenReturn("admin@gmail.com", "kasun@gmail.com");
        when(resultSet.getInt("role_id")).thenReturn(1, 2);
        when(resultSet.getString("role")).thenReturn("ADMIN", "CUSTOMER");

        List<UserDTO> users = authBO.getAllUsers();
        assertNotNull(users);
        assertEquals(2, users.size());


        assertEquals(1, users.get(0).getId());
        assertEquals("Dimuthu Lakshan", users.get(0).getName());
        assertEquals("ADMIN", users.get(0).getRole().getName());

        // Verify the second user
        assertEquals(2, users.get(1).getId());
        assertEquals("Kasun Rajitha", users.get(1).getName());
        assertEquals("CUSTOMER", users.get(1).getRole().getName());
    }

    @Test
    public void testDeleteUser() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isDeleted = authBO.deleteUser(1);
        assertTrue(isDeleted);
    }
}