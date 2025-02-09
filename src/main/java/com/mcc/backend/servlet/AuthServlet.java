package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.AuthBO;
import com.mcc.backend.bo.custom.impl.AuthBOImpl;
import com.mcc.backend.dto.UserDTO;

import javax.annotation.Resource;
import javax.json.Json;
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
                    response.getWriter().write("User registered successfully");
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Registration failed");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new ServletException("Registration failed", e);
            }
        }
    }


    private JsonObject parseJson(InputStream inputStream) throws IOException {
        try (JsonReader jsonReader = Json.createReader(new InputStreamReader(inputStream, "UTF-8"))) {
            return jsonReader.readObject();
        }
    }
}