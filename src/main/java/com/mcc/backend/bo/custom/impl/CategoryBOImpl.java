package com.mcc.backend.bo.custom.impl;




import com.mcc.backend.bo.custom.CategoryBO;
import com.mcc.backend.dao.custom.CategoryDAO;
import com.mcc.backend.dao.custom.impl.CategoryDAOImpl;
import com.mcc.backend.dto.CategoryDTO;
import com.mcc.backend.entity.Category;
import com.mcc.backend.servlet.CategoryServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryBOImpl implements CategoryBO {
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    public List<CategoryDTO> getAllCategories(){
        List<CategoryDTO> categoryDTOs = new ArrayList<>();

        try (Connection connection = CategoryServlet.dataSource.getConnection()) {
            List<Category> categories = categoryDAO.getAllCategories(connection);

            for (Category category : categories) {
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setId(category.getId());
                categoryDTO.setName(category.getName());
                categoryDTO.setIcon(category.getIcon());
                categoryDTO.setTitle(category.getTitle());
                categoryDTO.setFeatures(category.getFeatures());
                categoryDTO.setPrice(category.getPrice());
                categoryDTOs.add(categoryDTO);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categoryDTOs;
    }


    @Override
    public List<CategoryDTO> getAllCategoryNames() throws SQLException {
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
        try (Connection connection = CategoryServlet.dataSource.getConnection()) {
            List<Category> categories = categoryDAO.getAllCategoryNames(connection);

            for (Category category : categories) {
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setId(category.getId());
                categoryDTO.setName(category.getName());
                categoryDTOs.add(categoryDTO);
            }
        }
        return categoryDTOs;
    }

    @Override
    public boolean updateCategoryPrice(int id, double newPrice) throws SQLException {
        try (Connection connection = CategoryServlet.dataSource.getConnection()) {
            return categoryDAO.updateCategoryPrice(connection, id, newPrice);
        }
    }
}