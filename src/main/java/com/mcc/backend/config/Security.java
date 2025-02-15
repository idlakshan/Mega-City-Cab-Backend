package com.mcc.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

public class Security {

    private static final String SECRET_KEY = "megacitycab";

    public static String createJWT(int userId, String role) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTime = currentTimeMillis + (24 * 60 * 60 * 1000);

        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());

        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(expirationTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public static Jws<Claims> isValidCustomerJWT(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder response = Json.createObjectBuilder();
        PrintWriter writer = resp.getWriter();
        try {

            String authHeader = req.getHeader("Authorization");

            System.out.println(authHeader);

            resp.setContentType("application/json");

            if (authHeader != null) {
                String token = authHeader.substring(7);

                Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
                Jws<Claims> claims = getIDFromJWT(token);
                Object role = claims.getBody().get("role");

                if (Objects.equals(role, "CUSTOMER")){
                    return getIDFromJWT(token);
                }
            }
            response.add("message", "Unauthorized Request");
            response.add("code", 403);
            resp.setStatus(403);

            writer.print(response.build());
            writer.close();
            return null;
        } catch (Exception e) {


            response.add("message", "Unauthorized Request");
            response.add("code", 403);
            resp.setStatus(403);

            writer.print(response.build());
            writer.close();
        }
        response.add("message", "Unauthorized Request");
        response.add("code", 403);
        resp.setStatus(403);

        writer.print(response.build());
        writer.close();
        return null;
    }

    public static Jws<Claims> isValidAdminJWT(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder response = Json.createObjectBuilder();
        PrintWriter writer = resp.getWriter();
        try {

            String authHeader = req.getHeader("Authorization");
            resp.setContentType("application/json");

            if (authHeader != null) {
                System.out.println("hiiiiiiiiii");
                // Extract the token from the Authorization header
                String token = authHeader.substring(7); // Remove "Bearer " prefix
                //System.out.println("Token "+token);
                Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
                // System.out.println("claims "+claimsJws);
                Jws<Claims> claims = getIDFromJWT(token);

                Object role = claims.getBody().get("role");
                System.out.println("role is "+role);

                if (Objects.equals(role, "ADMIN")){
                    return getIDFromJWT(token);
                }
            }
            response.add("message", "Unauthorized Request");
            response.add("code", 403);
            resp.setStatus(403);

            writer.print(response.build());
            writer.close();
            return null;
        } catch (Exception e) {


            response.add("message", "Unauthorized Request");
            response.add("code", 403);
            resp.setStatus(403);

            writer.print(response.build());
            writer.close();
        }
        response.add("message", "Unauthorized Request");
        response.add("code", 403);
        resp.setStatus(403);

        writer.print(response.build());
        writer.close();
        return null;
    }

    public static Jws<Claims> isValidJWT(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder response = Json.createObjectBuilder();
        PrintWriter writer = resp.getWriter();
        try {
            String authHeader = req.getHeader("Authorization");
            resp.setContentType("application/json");

            if (authHeader != null) {
                String token = authHeader.substring(7);
                return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
            }

            response.add("message", "Unauthorized Request");
            response.add("code", 403);
            resp.setStatus(403);

            writer.print(response.build());
            writer.close();
            return null;
        } catch (Exception e) {
            response.add("message", "Unauthorized Request");
            response.add("code", 403);
            resp.setStatus(403);

            writer.print(response.build());
            writer.close();
            return null;
        }
    }

    public static Jws<Claims> getIDFromJWT(String jwt) {
        return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(jwt);
    }
}