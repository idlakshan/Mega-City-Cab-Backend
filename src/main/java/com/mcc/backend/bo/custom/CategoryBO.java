package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.CategoryDTO;

import java.sql.SQLException;
import java.util.List;

public interface CategoryBO {
    List<CategoryDTO> getAllCategories() throws SQLException;
    List<CategoryDTO> getAllCategoryNames() throws SQLException;
    boolean updateCategoryPrice(int id, double newPrice) throws SQLException;
}