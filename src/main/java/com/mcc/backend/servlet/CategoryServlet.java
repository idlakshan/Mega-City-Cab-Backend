package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.CategoryBO;
import com.mcc.backend.bo.custom.impl.CategoryBOImpl;
import com.mcc.backend.config.Security;
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
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (path.equals("/all")) {

                if (Security.isValidJWT(req, resp) != null) {
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


                    responseBuilder.add("status", HttpServletResponse.SC_OK)
                            .add("message", "Success")
                            .add("data", Json.createObjectBuilder().add("categories", jsonArrayBuilder))
                            .add("error", Json.createObjectBuilder().add("message", ""));

                    resp.getWriter().write(responseBuilder.build().toString());
                } else {

                    responseBuilder.add("status", HttpServletResponse.SC_FORBIDDEN)
                            .add("message", "Unauthorized access")
                            .add("data", Json.createObjectBuilder())
                            .add("error", Json.createObjectBuilder().add("message", "You do not have permission to access this resource."));

                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().write(responseBuilder.build().toString());
                }
            } else if (path.equals("/category-name")) {

                if (Security.isValidAdminJWT(req, resp) != null) {
                    List<CategoryDTO> categories = categoryBO.getAllCategoryNames();
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

                    for (CategoryDTO category : categories) {
                        jsonArrayBuilder.add(Json.createObjectBuilder().add("name", category.getName()));
                    }


                    responseBuilder.add("status", HttpServletResponse.SC_OK)
                            .add("message", "Success")
                            .add("data", Json.createObjectBuilder().add("categories", jsonArrayBuilder))
                            .add("error", Json.createObjectBuilder().add("message", ""));

                    resp.getWriter().write(responseBuilder.build().toString());
                } else {
                    responseBuilder.add("status", HttpServletResponse.SC_FORBIDDEN)
                            .add("message", "Unauthorized access")
                            .add("data", Json.createObjectBuilder())
                            .add("error", Json.createObjectBuilder().add("message", "Only ADMIN users can access this resource."));

                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().write(responseBuilder.build().toString());
                }
            } else {

                responseBuilder.add("status", HttpServletResponse.SC_NOT_FOUND)
                        .add("message", "Endpoint not found")
                        .add("data", Json.createObjectBuilder())
                        .add("error", Json.createObjectBuilder().add("message", "The requested endpoint does not exist."));

                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(responseBuilder.build().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();


            responseBuilder.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .add("message", "Database error")
                    .add("data", Json.createObjectBuilder())
                    .add("error", Json.createObjectBuilder().add("message", e.getMessage()));

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(responseBuilder.build().toString());
        }
    }
}