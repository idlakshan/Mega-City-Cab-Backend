package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.bo.custom.impl.BookingBOImpl;
import com.mcc.backend.bo.custom.impl.CarBOImpl;
import com.mcc.backend.bo.custom.impl.DriverBOImpl;
import com.mcc.backend.config.Security;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

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

    private CarBO carBO = new CarBOImpl();
    private DriverBO driverBO = new DriverBOImpl();
    private BookingBO bookingBO = new BookingBOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject requestBody = Json.createReader(req.getReader()).readObject();
        int categoryId = requestBody.getInt("categoryId");

        try {
            List<CarDTO> availableCars = carBO.getAvailableVehiclesByCategory(categoryId);
            if (availableCars.isEmpty()) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "No available cars for the selected category. Please try again later.", null, "No cars available.");
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
            Jws<Claims> claims = Security.isValidAdminJWT(req, resp);
            if (claims == null) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Invalid or missing JWT.", null, null);
                return;
            }

            try {
                List<BookingDTO> allBookings = bookingBO.getAllBookings();
                JsonObject response = buildBookingResponse(allBookings);
                resp.setContentType("application/json");
                resp.getWriter().write(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
            }
        } else if (pathInfo.startsWith("/user/")) {
            Jws<Claims> claims = Security.isValidJWT(req, resp);
            if (claims == null) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Invalid or missing JWT.", null, null);
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 3) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid path format. Expected /user/{userId}.", null, null);
                return;
            }

            try {
                int userId = Integer.parseInt(pathParts[2]);
                List<BookingDTO> userBookings = bookingBO.getBookingsByUserId(userId);
                JsonObject response = buildBookingResponse(userBookings);
                resp.setContentType("application/json");
                resp.getWriter().write(response.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.", null, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
            }
        } else if (pathInfo.startsWith("/bookings-count/")) {
            Jws<Claims> claims = Security.isValidAdminJWT(req, resp);
            if (claims != null) {
                try {
                    System.out.println("works");
                    // Fetch stats data
                    int totalBookings = bookingBO.getTotalBookings();
                    int activeDrivers = driverBO.getActiveDrivers();
                    int availableVehicles = carBO.getAvailableVehicles();
                    double totalRevenue = bookingBO.getTotalRevenue();
                    System.out.println(totalBookings+" "+activeDrivers+" "+availableVehicles+" "+totalRevenue);

                    JsonObject data = Json.createObjectBuilder()
                            .add("totalBookings", totalBookings)
                            .add("activeDrivers", activeDrivers)
                            .add("availableVehicles", availableVehicles)
                            .add("totalRevenue", totalRevenue)
                            .build();

                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Statistics retrieved successfully!", data, null);

                    resp.setContentType("application/json");
                   // resp.getWriter().write(response.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
                }
            }
        }else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private JsonObject buildBookingResponse(List<BookingDTO> bookings) {
        JsonArrayBuilder bookingsArray = Json.createArrayBuilder();
        for (BookingDTO booking : bookings) {
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
        return Json.createObjectBuilder()
                .add("status", HttpServletResponse.SC_OK)
                .add("message", "Bookings retrieved successfully!")
                .add("data", bookingsArray)
                .build();
    }
}