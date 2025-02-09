package com.mcc.backend.servlet;

import com.mcc.backend.dto.CategoryDTO;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/category")
public class CategoryServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CategoryDTO> categories = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM category";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    CategoryDTO category = new CategoryDTO();
                    category.setName(resultSet.getString("name"));
                    category.setIcon(resultSet.getString("icon"));
                    category.setTitle(resultSet.getString("title"));
                    category.setFeatures(resultSet.getString("features"));
                    category.setPrice(resultSet.getDouble("price"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (CategoryDTO category : categories) {
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                    .add("name", category.getName())
                    .add("icon", category.getIcon())
                    .add("title", category.getTitle())
                    .add("features", category.getFeatures())
                    .add("price", category.getPrice());
            jsonArrayBuilder.add(jsonObjectBuilder);
        }


        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("categories", jsonArrayBuilder)
                .build();


        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonResponse.toString());
    }
}