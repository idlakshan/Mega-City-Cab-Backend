package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.bo.custom.impl.CarBOImpl;
import com.mcc.backend.bo.custom.impl.DriverBOImpl;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.util.ResponseUtil;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

@WebServlet("/booking/*")
public class BookingServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private  CarBO carBO = new CarBOImpl();
    private  DriverBO driverBO = new DriverBOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject requestBody = Json.createReader(req.getReader()).readObject();
        int categoryId = requestBody.getInt("categoryId");
        //System.out.println(categoryId);

        try {
            List<CarDTO> availableCars = carBO.getAvailableVehiclesByCategory(categoryId);
            if (availableCars.isEmpty()) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "No available cars for the selected category.Please try again later.", null, "No cars available.");
                return;
            }

            List<DriverDTO> availableDrivers = driverBO.getAvailableDrivers();
            if (availableDrivers.isEmpty()) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "All drivers are booked. Please try again later.", null, "No drivers available.");
                return;
            }

            Random random = new Random();
            CarDTO assignedCar = availableCars.get(random.nextInt(availableCars.size()));
            DriverDTO assignedDriver = availableDrivers.get(random.nextInt(availableDrivers.size()));

            JsonObject response = Json.createObjectBuilder()
                    .add("status", HttpServletResponse.SC_OK)
                    .add("message", "Car and driver assigned successfully!")
                    .add("data", Json.createObjectBuilder()
                            .add("car", Json.createObjectBuilder()
                                    .add("carId", assignedCar.getCarId())
                                    .add("carName", assignedCar.getCarName())
                                    .add("carImage", assignedCar.getCarImage()))
                            .add("driver", Json.createObjectBuilder()
                                    .add("driverId", assignedDriver.getDriverId())
                                    .add("driverName", assignedDriver.getDriverName())
                                    .add("licenseImage", assignedDriver.getLicenseImage())))
                    .build();

            resp.setContentType("application/json");
            resp.getWriter().write(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
        }
    }
}
