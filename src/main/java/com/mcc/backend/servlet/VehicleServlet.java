package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.bo.custom.impl.CarBOImpl;
import com.mcc.backend.config.Security;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;
import com.mcc.backend.util.ResponseUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@WebServlet(urlPatterns = "/vehicle/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50)
public class VehicleServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private final CarBO carBO = new CarBOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jws<Claims> claims = Security.isValidAdminJWT(req, resp);

        if (claims != null) {
            int categoryId;
            String categoryIdStr = req.getParameter("categoryId");
            String carName = req.getParameter("carName");
            String carNumber = req.getParameter("carNumber");

            if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid input", null, "categoryId is required");
                return;
            }

            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid input", null, "Invalid categoryId format");
                return;
            }

            Part filePart = req.getPart("carImage");
            if (filePart == null || filePart.getSize() <= 0) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid input", null, "No file uploaded");
                return;
            }

            String rootPath = "D:/Projects/ICBT/Mega City Cab/Backend";
            String uploadDir = rootPath + File.separator + "uploads/vehicles";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdir();
            }

            String imageFileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
            File file = new File(uploadDir, imageFileName);
            Files.copy(filePart.getInputStream(), file.toPath());

            CarDTO dto = new CarDTO();
            dto.setCategoryId(categoryId);
            dto.setCarName(carName);
            dto.setCarNumber(carNumber);
            dto.setCarImage(imageFileName);

            try {
                carBO.saveCar(dto);
                JsonObject data = Json.createObjectBuilder().add("carNumber", dto.getCarNumber()).build();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Car saved successfully", data, null);
            } catch (SQLException e) {
                if (e.getMessage().contains("Car number already exists")) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_CONFLICT, "Duplicate entry", null, "Car number already exists");
                } else {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error", null, e.getMessage());
                }
            } catch (ClassNotFoundException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Class not found", null, e.getMessage());
            }
        } else {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_FORBIDDEN, "Unauthorized access", null, "Only ADMIN users can add vehicles.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jws<Claims> claims = Security.isValidAdminJWT(req, resp);

        if (claims != null) {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
             
                try {
                    List<CarDTO> vehicles = carBO.getAllVehicles();
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    for (CarDTO vehicle : vehicles) {
                        JsonObject vehicleJson = Json.createObjectBuilder()
                                .add("id", vehicle.getCarId())
                                .add("categoryId", vehicle.getCategoryId())
                                .add("carName", vehicle.getCarName())
                                .add("carNumber", vehicle.getCarNumber())
                                .add("carImage", vehicle.getCarImage())
                                .add("status", vehicle.getStatus())
                                .build();
                        jsonArrayBuilder.add(vehicleJson);
                    }

                    JsonObject data = Json.createObjectBuilder().add("vehicles", jsonArrayBuilder).build();
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Vehicles retrieved successfully", data, null);
                } catch (SQLException e) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error", null, e.getMessage());
                } catch (ClassNotFoundException e) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Class not found", null, e.getMessage());
                }
            } else {
                try {
                    int carId = Integer.parseInt(pathInfo.substring(1));
                    CarDTO vehicle = carBO.getVehicleById(carId);

                    if (vehicle != null) {
                        JsonObject vehicleJson = Json.createObjectBuilder()
                                .add("id", vehicle.getCarId())
                                .add("categoryId", vehicle.getCategoryId())
                                .add("carName", vehicle.getCarName())
                                .add("carNumber", vehicle.getCarNumber())
                                .add("carImage", vehicle.getCarImage())
                                .add("status", vehicle.getStatus())
                                .build();

                        ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Vehicle retrieved successfully", vehicleJson, null);
                    } else {
                        ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Vehicle not found", null, "No vehicle found with ID: " + carId);
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid car ID format", null, "Car ID must be a number.");
                } catch (SQLException e) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error", null, e.getMessage());
                } catch (ClassNotFoundException e) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Class not found", null, e.getMessage());
                }
            }
        } else {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_FORBIDDEN, "Unauthorized access", null, "You do not have permission to access this resource.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing car ID", null, "Car ID is required.");
            return;
        }

        int carId;
        try {
            carId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid car ID format", null, "Car ID must be a number.");
            return;
        }

        try {
            boolean deleted = carBO.deleteCar(carId);
            if (deleted) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Car deleted successfully", null, null);
            } else {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Car not found", null, "No car found with ID: " + carId);
            }
        } catch (SQLException e) {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred", null, e.getMessage());
        } catch (ClassNotFoundException e) {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred", null, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jws<Claims> claims = Security.isValidAdminJWT(req, resp);

        if (claims != null) {

            if (!req.getContentType().startsWith("multipart/form-data")) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format.", null, "Request must be multipart/form-data.");
                return;
            }

            int id;
            int categoryId;
            String idStr = req.getParameter("carId");
            String categoryIdStr = req.getParameter("categoryId");
            String carName = req.getParameter("carName");
//            String province = req.getParameter("province");
            String carNumber = req.getParameter("carNumber");
            System.out.println(carNumber);

            String status = req.getParameter("status");



            try {
                id = Integer.parseInt(idStr);
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid input", null, "Invalid ID or categoryId format.");
                return;
            }


            Part filePart = req.getPart("carImage");
            String carImageFileName = null;

            if (filePart != null && filePart.getSize() > 0) {
                String rootPath = "D:/Projects/ICBT/Mega City Cab/Backend";
                String uploadDir = rootPath + File.separator + "uploads/vehicles";
                File uploadFolder = new File(uploadDir);

                if (!uploadFolder.exists()) {
                    uploadFolder.mkdir();
                }

                carImageFileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
                File file = new File(uploadDir, carImageFileName);
                Files.copy(filePart.getInputStream(), file.toPath());
            }


            CarDTO dto = new CarDTO();
            dto.setCarId(id);
            dto.setCategoryId(categoryId);
            dto.setCarName(carName);
            dto.setCarNumber(carNumber);
            dto.setCarImage(carImageFileName);
            dto.setStatus(status);


            try {
                boolean isUpdated = carBO.updateVehicle(dto);
                if (isUpdated) {
                    JsonObject data = Json.createObjectBuilder()
                            .add("carId", dto.getCarId())
                            .add("carName", dto.getCarName())
                            .add("carNumber", dto.getCarNumber())
                            .add("status", dto.getStatus())
                            .build();

                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Vehicle updated successfully!", data, null);
                } else {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update vehicle.", null, "Database update failed.");
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("Car number already exists")) {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_CONFLICT, "Duplicate entry", null, "Car number already exists.");
                } else {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error", null, e.getMessage());
                }
            } catch (ClassNotFoundException e) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Class not found", null, e.getMessage());
            }
        } else {
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_FORBIDDEN, "Unauthorized access", null, "Only ADMIN users can update vehicles.");
        }
    }
}