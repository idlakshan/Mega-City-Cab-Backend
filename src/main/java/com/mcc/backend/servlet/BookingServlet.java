package com.mcc.backend.servlet;

import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.bo.custom.PaymentBO;
import com.mcc.backend.bo.custom.impl.BookingBOImpl;
import com.mcc.backend.bo.custom.impl.CarBOImpl;
import com.mcc.backend.bo.custom.impl.DriverBOImpl;
import com.mcc.backend.bo.custom.impl.PaymentBOImpl;
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
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

@WebServlet("/booking/*")
public class BookingServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private CarBO carBO = new CarBOImpl();
    private DriverBO driverBO = new DriverBOImpl();
    private BookingBO bookingBO = new BookingBOImpl();
    private PaymentBO paymentBO = new PaymentBOImpl();

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
                JsonArrayBuilder bookingsArrayBuilder = Json.createArrayBuilder();
                for (BookingDTO booking : allBookings) {
                    JsonObjectBuilder bookingBuilder = Json.createObjectBuilder()
                            .add("bookingId", booking.getBookingId())
                            .add("userId", booking.getUserId())
                            .add("pickupLocation", booking.getPickupLocation())
                            .add("dropLocation", booking.getDropLocation())
                            .add("bookingDateTime", booking.getBookingDateTime().toString())
                            .add("customerName", booking.getCustomerName())
                            .add("customerEmail", booking.getCustomerEmail())
                            .add("customerPhone", booking.getCustomerPhone())
                            .add("status", booking.getStatus());

                    CarDTO car = booking.getCar();
                    if (car != null) {
                        JsonObjectBuilder carBuilder = Json.createObjectBuilder()
                                .add("carId", car.getCarId())
                                .add("carName", car.getCarName())
                                .add("carNumber", car.getCarNumber());
                        bookingBuilder.add("car", carBuilder);
                    }

                    DriverDTO driver = booking.getDriver();
                    if (driver != null) {
                        JsonObjectBuilder driverBuilder = Json.createObjectBuilder()
                                .add("driverId", driver.getDriverId())
                                .add("driverName", driver.getDriverName())
                                .add("driverNic", driver.getDriverNic());
                        bookingBuilder.add("driver", driverBuilder);
                    }

                    bookingsArrayBuilder.add(bookingBuilder);
                }

                JsonObject response = Json.createObjectBuilder()
                        .add("bookings", bookingsArrayBuilder)
                        .build();

                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "All Bookings retrieved successfully!", response, null);
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
        } else if (pathInfo.startsWith("/bookings-count")) {
            Jws<Claims> claims = Security.isValidAdminJWT(req, resp);
            if (claims != null) {
                try {
                  //  System.out.println("works");

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


                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
                }
            }
        }else if (pathInfo.startsWith("/last-7-days-data")) {
            Jws<Claims> claims = Security.isValidAdminJWT(req, resp);
            if (claims != null) {
                try {

                    Map<String, Double> paymentsLast7Days = paymentBO.getTotalPaymentsLast7Days();
                    Map<String, Integer> bookingsLast7Days = bookingBO.getBookingCountsLast7Days();


                    JsonObjectBuilder paymentsBuilder = Json.createObjectBuilder();
                    for (Map.Entry<String, Double> entry : paymentsLast7Days.entrySet()) {
                        paymentsBuilder.add(entry.getKey(), entry.getValue());
                    }

                    JsonObjectBuilder bookingsBuilder = Json.createObjectBuilder();
                    for (Map.Entry<String, Integer> entry : bookingsLast7Days.entrySet()) {
                        bookingsBuilder.add(entry.getKey(), entry.getValue());
                    }

                    JsonObject data = Json.createObjectBuilder()
                            .add("paymentsLast7Days", paymentsBuilder.build())
                            .add("bookingsLast7Days", bookingsBuilder.build())
                            .build();

                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Last 7 days data retrieved successfully!", data, null);

                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
                }
            }
        }else if (pathInfo.startsWith("/status")) {
            Jws<Claims> claims = Security.isValidAdminJWT(req, resp);
            if (claims != null) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Invalid or missing JWT.", null, null);
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 3) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid path format. Expected /status/{status}.", null, null);
                return;
            }

            try {
                String status = pathParts[2];
                List<BookingDTO> bookingsByStatus = bookingBO.getBookingsByStatus(status);

                JsonArrayBuilder bookingsArrayBuilder = Json.createArrayBuilder();
                for (BookingDTO booking : bookingsByStatus) {
                    JsonObjectBuilder bookingBuilder = Json.createObjectBuilder()
                            .add("bookingId", booking.getBookingId())
                            .add("userId", booking.getUserId())
                            .add("pickupLocation", booking.getPickupLocation())
                            .add("dropLocation", booking.getDropLocation())
                            .add("bookingDateTime", booking.getBookingDateTime().toString())
                            .add("customerName", booking.getCustomerName())
                            .add("customerEmail", booking.getCustomerEmail())
                            .add("customerPhone", booking.getCustomerPhone())
                            .add("status", booking.getStatus());

                    CarDTO car = booking.getCar();
                    if (car != null) {
                        JsonObjectBuilder carBuilder = Json.createObjectBuilder()
                                .add("carId", car.getCarId())
                                .add("carName", car.getCarName())
                                .add("carNumber", car.getCarNumber());
                        bookingBuilder.add("car", carBuilder);
                    }

                    DriverDTO driver = booking.getDriver();
                    if (driver != null) {
                        JsonObjectBuilder driverBuilder = Json.createObjectBuilder()
                                .add("driverId", driver.getDriverId())
                                .add("driverName", driver.getDriverName())
                                .add("driverNic", driver.getDriverNic());
                        bookingBuilder.add("driver", driverBuilder);
                    }

                    bookingsArrayBuilder.add(bookingBuilder);
                }

                JsonObject response = Json.createObjectBuilder()
                        .add("bookings", bookingsArrayBuilder)
                        .build();

                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "InProgress Bookings retrieved successfully!", response, null);

            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
            }
        }else if (pathInfo.startsWith("/user-stats/")) {
            Jws<Claims> claims = Security.isValidJWT(req, resp);
            if (claims == null) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Invalid or missing JWT.", null, null);
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 3) {
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid path format. Expected /user-stats/{userId}.", null, null);
                return;
            }

            try {
                int userId = Integer.parseInt(pathParts[2]);
                int totalRides = bookingBO.getTotalBookingsByUserId(userId);
                double totalSpending = bookingBO.getTotalSpendingByUserId(userId);
                String activeSince = bookingBO.getActiveSinceByUserId(userId);
                String favoriteLocation = bookingBO.getFavoriteLocationByUserId(userId);

                JsonObject data = Json.createObjectBuilder()
                        .add("totalRides", totalRides)
                        .add("totalSpending", totalSpending)
                        .add("activeSince", activeSince)
                        .add("favoriteLocation", favoriteLocation)
                        .build();

                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "User stats retrieved successfully!", data, null);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.", null, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
            }
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jws<Claims> claims = Security.isValidAdminJWT(req, resp);
        if (claims != null) {
            JsonObject requestBody = Json.createReader(req.getReader()).readObject();
            int bookingId = requestBody.getInt("bookingId");
            String status = requestBody.getString("status");

            try {
                if ("Completed".equals(status)) {
                    bookingBO.updateBookingStatus(bookingId, status);
                    BookingDTO booking = bookingBO.getBookingById(bookingId);

                    bookingBO.updateBookingCarStatus(booking.getCarId(), "Available");

                    bookingBO.updateBookingDriverStatus(booking.getDriverId(), "Available");

                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_OK, "Booking status updated to Completed and car/driver status updated to Available.", null, null);
                } else {
                    ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid status update.", null, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.", null, e.getMessage());
            }
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