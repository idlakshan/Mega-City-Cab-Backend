package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.bo.custom.impl.DriverBOImpl;
import com.mcc.backend.config.Security;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/driver/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50)
public class DriverServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private final DriverBO driverBO = new DriverBOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jws<Claims> claims = Security.isValidAdminJWT(req, resp);

        if (claims != null) {
            if (!req.getContentType().startsWith("multipart/form-data")) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format.", null, "Request must be multipart/form-data.");
                return;
            }

            String driverName = req.getParameter("driverName");
            String driverNic = req.getParameter("driverNic");
            String driverAddress = req.getParameter("driverAddress");
            String driverEmail = req.getParameter("driverEmail");
            String driverContact = req.getParameter("driverContact");

            System.out.println(driverName+" "+driverNic+" "+driverAddress+" "+driverEmail+" "+driverContact);


            if (driverName == null || driverName.trim().isEmpty() || driverNic == null || driverNic.trim().isEmpty() ||
                    driverAddress == null || driverAddress.trim().isEmpty() || driverContact == null || driverContact.trim().isEmpty()) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid input", null, "All fields are required.");
                return;
            }

            Part filePart = req.getPart("licenseImage");
            String licenseImageFileName = null;

            if (filePart != null && filePart.getSize() > 0) {
                String rootPath = "D:/Projects/ICBT/Mega City Cab/Backend";
                String uploadDir = rootPath + File.separator + "uploads/driver";
                File uploadFolder = new File(uploadDir);

                if (!uploadFolder.exists()) {
                    uploadFolder.mkdir();
                }

                licenseImageFileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
                File file = new File(uploadDir, licenseImageFileName);
                Files.copy(filePart.getInputStream(), file.toPath());
            }

            DriverDTO driver = new DriverDTO();
            driver.setDriverName(driverName);
            driver.setDriverNic(driverNic);
            driver.setDriverAddress(driverAddress);
            driver.setDriverEmail(driverEmail);
            driver.setDriverContact(driverContact);
            driver.setLicenseImage(licenseImageFileName);


            try {
                boolean isSaved = driverBO.saveDriver(driver);
                if (isSaved) {
                    JsonObject data = Json.createObjectBuilder()
                            .add("driverName", driver.getDriverName())
                            .add("driverContact", driver.getDriverContact())
                            .build();

                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Driver saved successfully!", data, null);
                } else {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save driver.", null, "Database error.");
                }
            } catch (ClassNotFoundException | SQLException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Class not found", null, e.getMessage());
            }
        } else {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_FORBIDDEN, "Unauthorized access", null, "Only ADMIN users can add drivers.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                List<DriverDTO> drivers = driverBO.getAllDrivers();
                JsonArrayBuilder driversArray = Json.createArrayBuilder();

                for (DriverDTO driver : drivers) {
                    JsonObject driverJson = Json.createObjectBuilder()
                            .add("driverId", driver.getDriverId())
                            .add("driverName", driver.getDriverName())
                            .add("driverNic", driver.getDriverNic())
                            .add("driverAddress", driver.getDriverAddress())
                            .add("driverEmail", driver.getDriverEmail())
                            .add("licenseImage", driver.getLicenseImage())
                            .add("driverContact", driver.getDriverContact())
                            .add("status", driver.getStatus())
                            .build();
                    driversArray.add(driverJson);
                }

                JsonObject response = Json.createObjectBuilder()
                        .add("status", HttpServletResponse.SC_OK)
                        .add("message", "Drivers retrieved successfully!")
                        .add("data", driversArray)
                        .build();

                resp.setContentType("application/json");
                resp.getWriter().write(response.toString());
            } catch (SQLException | ClassNotFoundException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error", null, e.getMessage());
            }
        } else {

            try {
                int driverId = Integer.parseInt(pathInfo.substring(1));
                DriverDTO driver = driverBO.getDriverById(driverId);

                if (driver != null) {
                    JsonObject driverJson = Json.createObjectBuilder()
                            .add("driverId", driver.getDriverId())
                            .add("driverName", driver.getDriverName())
                            .add("driverNic", driver.getDriverNic())
                            .add("driverAddress", driver.getDriverAddress())
                            .add("driverEmail", driver.getDriverEmail())
                            .add("licenseImage", driver.getLicenseImage())
                            .add("driverContact", driver.getDriverContact())
                            .add("status", driver.getStatus())
                            .build();

                    JsonObject response = Json.createObjectBuilder()
                            .add("status", HttpServletResponse.SC_OK)
                            .add("message", "Driver retrieved successfully!")
                            .add("data", driverJson)
                            .build();

                    resp.setContentType("application/json");
                    resp.getWriter().write(response.toString());
                } else {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Driver not found", null, "No driver found with ID: " + driverId);
                }
            } catch (NumberFormatException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid input", null, "Driver ID must be a number.");
            } catch (SQLException | ClassNotFoundException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error", null, e.getMessage());
            }
        }
    }
}