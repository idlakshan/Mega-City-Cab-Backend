package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.bo.custom.impl.BookingBOImpl;
import com.mcc.backend.bo.custom.impl.CarBOImpl;
import com.mcc.backend.bo.custom.impl.DriverBOImpl;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.util.ResponseUtil;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
    private BookingBO bookingBO = new BookingBOImpl();

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
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                List<BookingDTO> allBookings = bookingBO.getAllBookings();
                JsonArrayBuilder bookingsArray = Json.createArrayBuilder();
                for (BookingDTO booking : allBookings) {
                    bookingsArray.add(Json.createObjectBuilder()
                            .add("bookingId", booking.getBookingId())
                            .add("userId", booking.getUserId())
                            .add("carId", booking.getCarId())
                            .add("driverId", booking.getDriverId())
                            .add("pickupLocation", booking.getPickupLocation())
                            .add("dropLocation", booking.getDropLocation())
                            .add("bookingDateTime", booking.getBookingDateTime().toString())
                            .add("customerName", booking.getCustomerName())
                            .add("customerEmail", booking.getCustomerEmail())
                            .add("customerPhone", booking.getCustomerPhone())
                            .add("status", booking.getStatus()));
                }
                JsonObject response = Json.createObjectBuilder()
                        .add("status", HttpServletResponse.SC_OK)
                        .add("message", "All bookings retrieved successfully!")
                        .add("data", bookingsArray)
                        .build();
                resp.setContentType("application/json");
                resp.getWriter().write(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
            }
        } else if (pathInfo.startsWith("/user/")) {

            try {
                int userId = Integer.parseInt(pathInfo.split("/")[2]);
                List<BookingDTO> userBookings = bookingBO.getBookingsByUserId(userId);
                JsonArrayBuilder bookingsArray = Json.createArrayBuilder();
                for (BookingDTO booking : userBookings) {
                    bookingsArray.add(Json.createObjectBuilder()
                            .add("bookingId", booking.getBookingId())
                            .add("userId", booking.getUserId())
                            .add("carId", booking.getCarId())
                            .add("driverId", booking.getDriverId())
                            .add("pickupLocation", booking.getPickupLocation())
                            .add("dropLocation", booking.getDropLocation())
                            .add("bookingDateTime", booking.getBookingDateTime().toString())
                            .add("customerName", booking.getCustomerName())
                            .add("customerEmail", booking.getCustomerEmail())
                            .add("customerPhone", booking.getCustomerPhone())
                            .add("status", booking.getStatus()));
                }
                JsonObject response = Json.createObjectBuilder()
                        .add("status", HttpServletResponse.SC_OK)
                        .add("message", "Bookings retrieved successfully!")
                        .add("data", bookingsArray)
                        .build();
                resp.setContentType("application/json");
                resp.getWriter().write(response.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.", null, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
