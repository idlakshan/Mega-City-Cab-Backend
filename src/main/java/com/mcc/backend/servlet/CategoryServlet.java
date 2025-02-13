package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.CategoryBO;
import com.mcc.backend.bo.custom.impl.CategoryBOImpl;
import com.mcc.backend.config.Security;
import com.mcc.backend.dto.CategoryDTO;
import com.mcc.backend.util.ResponseUtil;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
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

        try {
            if (path.equals("/all")) {
                    List<CategoryDTO> categories = categoryBO.getAllCategories();
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

                    for (CategoryDTO category : categories) {
                        jsonArrayBuilder.add(Json.createObjectBuilder()
                                .add("name", category.getName())
                                .add("icon", category.getIcon())
                                .add("title", category.getTitle())
                                .add("features", category.getFeatures())
                                .add("price", category.getPrice()));
                    }

                    JsonObject data = Json.createObjectBuilder().add("categories", jsonArrayBuilder).build();
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Success", data, null);

            } else if (path.equals("/category-name")) {
                if (Security.isValidAdminJWT(req, resp) != null) {
                    List<CategoryDTO> categories = categoryBO.getAllCategoryNames();
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

                    for (CategoryDTO category : categories) {
                        jsonArrayBuilder.add(Json.createObjectBuilder()
                                .add("id", category.getId())
                                .add("name", category.getName()));
                    }

                    JsonObject data = Json.createObjectBuilder().add("categories", jsonArrayBuilder).build();
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Success", data, null);
                } else {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_FORBIDDEN, "Unauthorized access", null, "Only ADMIN users can access this resource.");
                }
            } else {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found", null, "The requested endpoint does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error", null, e.getMessage());
        }
    }
}
