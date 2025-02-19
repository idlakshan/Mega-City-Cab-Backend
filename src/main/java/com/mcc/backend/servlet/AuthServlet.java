package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.AuthBO;
import com.mcc.backend.bo.custom.impl.AuthBOImpl;
import com.mcc.backend.config.Security;
import com.mcc.backend.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private final AuthBO authBO = new AuthBOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();


        JsonObject jsonObject = parseJson(request.getInputStream());

        if (path.equals("/login")) {
            String email = jsonObject.getString("email", null);
            String password = jsonObject.getString("password", null);
            System.out.println("Login request - Email: " + email + ", Password: " + password);

            try {
                String token = authBO.login(email, password);
                if (token != null) {
                    response.getWriter().write(token);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new ServletException("Login failed", e);
            }
        } else if (path.equals("/signup")) {
            String name = jsonObject.getString("name", null);
            String nic = jsonObject.getString("nic", null);
            String phone = jsonObject.getString("phone", null);
            String email = jsonObject.getString("email", null);
            String password = jsonObject.getString("password", null);

            System.out.println("Signup request - Name: " + name + ", NIC: " + nic + ", Phone: " + phone + ", Email: " + email + ", Password: " + password);

            UserDTO user = new UserDTO();
            user.setName(name);
            user.setNic(nic);
            user.setPhone(phone);
            user.setEmail(email);
            user.setPassword(password);

            try {
                if (authBO.signUp(user)) {
                    response.setContentType("application/json");
                    response.getWriter().write(
                            Json.createObjectBuilder()
                                    .add("status", "success")
                                    .add("message", "User registered successfully")
                                    .build()
                                    .toString()
                    );
                } else {
                    response.setContentType("application/json"); // Set response type to JSON
                    response.getWriter().write(
                            Json.createObjectBuilder()
                                    .add("status", "error")
                                    .add("message", "Registration failed")
                                    .build()
                                    .toString()
                    );
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new ServletException("Registration failed", e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path.equals("/current-user")) {
            try {
                Jws<Claims> claims = Security.isValidJWT(request, response);
                if (claims != null) {
                    int userId = (int) claims.getBody().get("userId");
                    UserDTO userDTO = authBO.getUserById(userId);
                    if (userDTO != null) {
                        response.setContentType("application/json");
                        response.getWriter().write(Json.createObjectBuilder()
                                .add("name", userDTO.getName())
                                .add("email", userDTO.getEmail())
                                .add("phone", userDTO.getPhone())
                                .build().toString());
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    }
                }
            } catch (SQLException e) {
                throw new ServletException("Failed to fetch user details", e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (path.equals("/all-users")) {
            try {
                Jws<Claims> claims = Security.isValidAdminJWT(request, response); // Ensure only admins can access
                if (claims == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Invalid or missing JWT.");
                    return;
                }

                List<UserDTO> users = authBO.getAllUsers();
                JsonArrayBuilder usersArray = Json.createArrayBuilder();
                for (UserDTO user : users) {
                    usersArray.add(Json.createObjectBuilder()
                            .add("id", user.getId())
                            .add("name", user.getName())
                            .add("nic", user.getNic())
                            .add("phone", user.getPhone())
                            .add("email", user.getEmail())
                            .add("role", Json.createObjectBuilder() // Include role
                                    .add("id", user.getRole().getId())
                                    .add("name", user.getRole().getName())));
                }
                JsonObject responseJson = Json.createObjectBuilder()
                        .add("status", HttpServletResponse.SC_OK)
                        .add("message", "All users retrieved successfully!")
                        .add("data", usersArray)
                        .build();
                response.setContentType("application/json");
                response.getWriter().write(responseJson.toString());
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path != null && path.startsWith("/delete/")) {
            try {
                Jws<Claims> claims = Security.isValidAdminJWT(request, response); // Ensure only admins can delete users
                if (claims == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Invalid or missing JWT.");
                    return;
                }

                int userId = Integer.parseInt(path.split("/")[2]); // Extract user ID from the path
                boolean isDeleted = authBO.deleteUser(userId);

                if (isDeleted) {
                    JsonObject responseJson = Json.createObjectBuilder()
                            .add("status", HttpServletResponse.SC_OK)
                            .add("message", "User and associated details deleted successfully!")
                            .build();
                    response.setContentType("application/json");
                    response.getWriter().write(responseJson.toString());
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found or could not be deleted.");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    private JsonObject parseJson(InputStream inputStream) throws IOException {
        try (JsonReader jsonReader = Json.createReader(new InputStreamReader(inputStream, "UTF-8"))) {
            return jsonReader.readObject();
        }
    }
}