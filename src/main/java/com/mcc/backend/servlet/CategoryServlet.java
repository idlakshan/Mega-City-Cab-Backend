package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.CategoryBO;
import com.mcc.backend.bo.custom.impl.CategoryBOImpl;
import com.mcc.backend.config.Security;
import com.mcc.backend.dto.CategoryDTO;
import com.mcc.backend.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.annotation.Resource;
import javax.json.*;
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
                                .add("id", category.getId())
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Jws<Claims> validAdminJWT = Security.isValidAdminJWT(req, resp);

        if (validAdminJWT != null) {
            try (JsonReader jsonReader = Json.createReader(req.getReader())) {
                JsonObject jsonObject = jsonReader.readObject();

                if (!jsonObject.containsKey("id") || !jsonObject.containsKey("price")) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: id, price", null, "Missing required parameters: id, price");
                    return;
                }
                int id;
                double newPrice;
                try {
                    id = jsonObject.getInt("id");
                    newPrice = jsonObject.getJsonNumber("price").doubleValue();
                } catch (Exception e) {ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters: id must be an integer, price must be a number", null, "Invalid parameters: id must be an integer, price must be a number");
                    return;
                }
                try {
                    boolean updated = categoryBO.updateCategoryPrice(id, newPrice);

                    if (updated) {ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Category price updated successfully", Json.createObjectBuilder().add("id", id).add("price", newPrice).build(), null);
                    } else {
                        ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found with id: " + id, null, "Category not found with id: " + id);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update category price", null, e.getMessage());
                }
            } catch (Exception e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format", null, e.getMessage());
            }
        } else {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid or missing JWT token", null, "Unauthorized: Invalid or missing JWT token"
            );
        }
    }
}
