package com.mcc.backend.servlet;


import com.mcc.backend.bo.custom.CategoryBO;
import com.mcc.backend.bo.custom.impl.CategoryBOImpl;
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
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/category/*")
public class CategoryServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private final CategoryBO categoryBO = new CategoryBOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo();

        if (path.equals("/all")) {
            try {
                List<CategoryDTO> categories = categoryBO.getAllCategories();

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
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            }
        }else if (path.equals("/category-name")) {
            try {
                List<CategoryDTO> categories = categoryBO.getAllCategoryNames();
                JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

                for (CategoryDTO category : categories) {
                    jsonArrayBuilder.add(Json.createObjectBuilder()
                            .add("name", category.getName()));
                }

                JsonObject jsonResponse = Json.createObjectBuilder()
                        .add("categories", jsonArrayBuilder)
                        .build();

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(jsonResponse.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
}