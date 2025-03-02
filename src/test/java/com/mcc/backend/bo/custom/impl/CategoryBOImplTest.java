package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.dao.custom.CategoryDAO;
import com.mcc.backend.dto.CategoryDTO;
import com.mcc.backend.entity.Category;
import com.mcc.backend.servlet.CategoryServlet;
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

public class CategoryBOImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private CategoryDAO categoryDAO;

    private CategoryBOImpl categoryBO;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        categoryBO = new CategoryBOImpl();
        CategoryServlet.dataSource = dataSource;
    }

    @Test
    public void testGetAllCategories() throws SQLException {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id")).thenReturn(1, 2);
        when(resultSet.getString("name")).thenReturn("Sedan", "SUV");
        when(resultSet.getString("icon")).thenReturn("sedan-icon.png", "suv-icon.png");
        when(resultSet.getString("title")).thenReturn("Comfortable Sedan", "Spacious SUV");
        when(resultSet.getString("features")).thenReturn("AC, Leather Seats", "AC, Sunroof");
        when(resultSet.getDouble("price")).thenReturn(50.0, 70.0);

        List<CategoryDTO> categories = categoryBO.getAllCategories();
        assertNotNull(categories);
        assertEquals(2, categories.size());

        assertEquals(1, categories.get(0).getId());
        assertEquals("Sedan", categories.get(0).getName());
        assertEquals("sedan-icon.png", categories.get(0).getIcon());
        assertEquals("Comfortable Sedan", categories.get(0).getTitle());
        assertEquals("AC, Leather Seats", categories.get(0).getFeatures());
        assertEquals(50.0, categories.get(0).getPrice());

        assertEquals(2, categories.get(1).getId());
        assertEquals("SUV", categories.get(1).getName());
        assertEquals("suv-icon.png", categories.get(1).getIcon());
        assertEquals("Spacious SUV", categories.get(1).getTitle());
        assertEquals("AC, Sunroof", categories.get(1).getFeatures());
        assertEquals(70.0, categories.get(1).getPrice());
    }

    @Test
    public void testGetAllCategoryNames() throws SQLException {
        // Mock the ResultSet to return two category names
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false); // Two rows
        when(resultSet.getInt("id")).thenReturn(1, 2);
        when(resultSet.getString("name")).thenReturn("Sedan", "SUV");

        List<CategoryDTO> categories = categoryBO.getAllCategoryNames();
        assertNotNull(categories);
        assertEquals(2, categories.size());

        assertEquals(1, categories.get(0).getId());
        assertEquals("Sedan", categories.get(0).getName());

        assertEquals(2, categories.get(1).getId());
        assertEquals("SUV", categories.get(1).getName());
    }

    @Test
    public void testUpdateCategoryPrice() throws SQLException {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isUpdated = categoryBO.updateCategoryPrice(1, 60.0);
        assertTrue(isUpdated);


        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateCategoryPriceFailure() throws SQLException {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean isUpdated = categoryBO.updateCategoryPrice(1, 60.0);
        assertFalse(isUpdated);

        verify(preparedStatement, times(1)).executeUpdate();
    }
}