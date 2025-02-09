package com.mcc.backend.servlet;

import com.mcc.backend.config.Security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@WebServlet(urlPatterns = "/vehicle")
public class VehicleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Authorization Header: " + req.getHeader("Authorization")); // Debug log
        Jws<Claims> validAdminJWT = Security.isValidAdminJWT(req, resp);

        if (!Objects.equals(validAdminJWT, null)) {

            Object role = validAdminJWT.getBody().get("role");
            resp.getWriter().println("Admin Works");

            System.out.println(role);
        }
    }

}
