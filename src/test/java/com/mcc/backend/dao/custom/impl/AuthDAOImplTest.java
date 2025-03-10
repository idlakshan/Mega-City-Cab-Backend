package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.entity.Role;
import com.mcc.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthDAOImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private AuthDAOImpl authDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
    }

    @Test
    void testFindUserIdByEmailAndPassword() throws SQLException {
        String email = "test@example.com";
        String password = "password123";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        int userId = authDAO.findUserIdByEmailAndPassword(connection, email, password);

        assertEquals(1, userId);
        verify(preparedStatement, times(1)).setString(1, email);
        verify(preparedStatement, times(1)).setString(2, password);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testFindRoleByUserId() throws SQLException {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("ADMIN");

        String role = authDAO.findRoleByUserId(connection, userId);

        assertEquals("ADMIN", role);
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testSaveUser() throws SQLException {
        User user = new User();
        user.setName("John Doe");
        user.setNic("123456789V");
        user.setPhone("1234567890");
        user.setEmail("john@example.com");
        user.setPassword("password123");

        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);


        int userId = authDAO.saveUser(connection, user);

        assertEquals(1, userId);
        verify(preparedStatement, times(1)).setString(1, "John Doe");
        verify(preparedStatement, times(1)).setString(2, "123456789V");
        verify(preparedStatement, times(1)).setString(3, "1234567890");
        verify(preparedStatement, times(1)).setString(4, "john@example.com");
        verify(preparedStatement, times(1)).setString(5, "password123");
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testSaveUserDetails() throws SQLException {
        int userId = 1;
        int roleId = 1;

        when(preparedStatement.executeUpdate()).thenReturn(1);

        authDAO.saveUserDetails(connection, userId, roleId);

        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).setInt(2, roleId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testFindRoleIdByName() throws SQLException {

        String roleName = "ADMIN";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);


        int roleId = authDAO.findRoleIdByName(connection, roleName);


        assertEquals(1, roleId);
        verify(preparedStatement, times(1)).setString(1, roleName);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testDeleteUserDetails() throws SQLException {
        int userId = 1;

        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        authDAO.deleteUserDetails(connection, userId);

        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeletePaymentsByUserId() throws SQLException {

        int userId = 1;

        when(preparedStatement.executeUpdate()).thenReturn(1);

        authDAO.deletePaymentsByUserId(connection, userId);

        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteBookingsByUserId() throws SQLException {
        int userId = 1;

        when(preparedStatement.executeUpdate()).thenReturn(1);

        authDAO.deleteBookingsByUserId(connection, userId);

        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testFindById() throws SQLException {

        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("John Doe");
        when(resultSet.getString("email")).thenReturn("john@example.com");
        when(resultSet.getString("phone")).thenReturn("1234567890");

        User user = authDAO.findById(connection, userId);

        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testFindAll() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("John Doe");
        when(resultSet.getString("nic")).thenReturn("123456789V");
        when(resultSet.getString("phone")).thenReturn("1234567890");
        when(resultSet.getString("email")).thenReturn("john@example.com");
        when(resultSet.getInt("role_id")).thenReturn(1);
        when(resultSet.getString("role")).thenReturn("ADMIN");

        List<User> users = authDAO.findAll(connection);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("ADMIN", users.get(0).getRole().getName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdate() throws SQLException {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhone("1234567890");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = authDAO.update(connection, user);

        assertTrue(result);
        verify(preparedStatement, times(1)).setString(1, "John Doe");
        verify(preparedStatement, times(1)).setString(2, "john@example.com");
        verify(preparedStatement, times(1)).setString(3, "1234567890");
        verify(preparedStatement, times(1)).setInt(4, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {
        int userId = 1;

        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = authDAO.delete(connection, userId);

        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeUpdate();
    }
}