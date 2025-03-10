package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.entity.Category;
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

class CategoryDAOImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private CategoryDAOImpl categoryDAO;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testFindAll() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Sedan");
        when(resultSet.getString("icon")).thenReturn("sedan-icon.png");
        when(resultSet.getString("title")).thenReturn("Sedan Cars");
        when(resultSet.getString("features")).thenReturn("Comfortable, Spacious");
        when(resultSet.getDouble("price")).thenReturn(50.0);

        List<Category> categories = categoryDAO.findAll(connection);

        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Sedan", categories.get(0).getName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetAllCategoryNames() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Sedan");

        List<Category> categories = categoryDAO.getAllCategoryNames(connection);

        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Sedan", categories.get(0).getName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateCategoryPrice() throws SQLException {
        int categoryId = 1;
        double newPrice = 60.0;
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = categoryDAO.updateCategoryPrice(connection, categoryId, newPrice);

        assertTrue(result);
        verify(preparedStatement, times(1)).setDouble(1, newPrice);
        verify(preparedStatement, times(1)).setInt(2, categoryId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testFindById() throws SQLException {
        int categoryId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Sedan");
        when(resultSet.getString("icon")).thenReturn("sedan-icon.png");
        when(resultSet.getString("title")).thenReturn("Sedan Cars");
        when(resultSet.getString("features")).thenReturn("Comfortable, Spacious");
        when(resultSet.getDouble("price")).thenReturn(50.0);

        Category category = categoryDAO.findById(connection, categoryId);

        assertNull(category);
        verify(preparedStatement, times(0)).setInt(1, categoryId);
    }

    @Test
    void testSave() throws SQLException {

        Category category = new Category();
        category.setName("Sedan");
        category.setIcon("sedan-icon.png");
        category.setTitle("Sedan Cars");
        category.setFeatures("Comfortable, Spacious");
        category.setPrice(50.0);


        boolean result = categoryDAO.save(connection, category);

        assertFalse(result);
        verify(preparedStatement, times(0)).executeUpdate();
    }

    @Test
    void testUpdate() throws SQLException {

        Category category = new Category();
        category.setId(1);
        category.setName("Sedan");
        category.setIcon("sedan-icon.png");
        category.setTitle("Sedan Cars");
        category.setFeatures("Comfortable, Spacious");
        category.setPrice(50.0);


        boolean result = categoryDAO.update(connection, category);


        assertFalse(result);
        verify(preparedStatement, times(0)).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {

        int categoryId = 1;


        boolean result = categoryDAO.delete(connection, categoryId);

        assertFalse(result);
        verify(preparedStatement, times(0)).executeUpdate();
    }
}