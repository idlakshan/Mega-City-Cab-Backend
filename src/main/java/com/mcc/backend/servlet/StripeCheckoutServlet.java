package com.mcc.backend.servlet;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.bo.custom.PaymentBO;
import com.mcc.backend.bo.custom.impl.BookingBOImpl;
import com.mcc.backend.bo.custom.impl.PaymentBOImpl;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.dto.PaymentDTO;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@WebServlet("/create-checkout-session")
public class StripeCheckoutServlet extends HttpServlet {

    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private BookingBO bookingBO = new BookingBOImpl();
    private PaymentBO paymentBO = new PaymentBOImpl();

    @Override
    public void init() throws ServletException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new ServletException("Unable to find config.properties");
            }
            prop.load(input);
            Stripe.apiKey = prop.getProperty("stripe.secret.key");
            if (Stripe.apiKey == null || Stripe.apiKey.isEmpty()) {
                throw new ServletException("Stripe API key not found in config.properties");
            }
        } catch (IOException e) {
            throw new ServletException("Failed to load config.properties", e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            String amountStr = req.getParameter("amount");
            double amount = Double.parseDouble(amountStr);
            String currency = req.getParameter("currency");
            String successUrl = req.getParameter("successUrl");
            String cancelUrl = req.getParameter("cancelUrl");
            String userId = req.getParameter("userId");
            String carId = req.getParameter("carId");
            String driverId = req.getParameter("driverId");
            String pickupLocation = req.getParameter("pickupLocation");
            String dropLocation = req.getParameter("dropLocation");
            String bookingDateTime = req.getParameter("bookingDateTime");
            String customerName = req.getParameter("customerName");
            String customerEmail = req.getParameter("customerEmail");
            String customerPhone = req.getParameter("customerPhone");

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(bookingDateTime, inputFormatter);
            String formattedDateTime = dateTime.format(outputFormatter);

            long amountInCents = (long) (amount * 100);

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency)
                                                    .setUnitAmount(amountInCents) // Amount in cents
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Ride Booking")
                                                                    .build())
                                                    .build())
                                    .setQuantity(1L)
                                    .build())
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .build();

            Session session = Session.create(params);


            BookingDTO bookingDTO = new BookingDTO();
            bookingDTO.setUserId(Integer.parseInt(userId));
            bookingDTO.setCarId(Integer.parseInt(carId));
            bookingDTO.setDriverId(Integer.parseInt(driverId));
            bookingDTO.setPickupLocation(pickupLocation);
            bookingDTO.setDropLocation(dropLocation);
            bookingDTO.setBookingDateTime(Timestamp.valueOf(formattedDateTime));
            bookingDTO.setCustomerName(customerName);
            bookingDTO.setCustomerEmail(customerEmail);
            bookingDTO.setCustomerPhone(customerPhone);
            bookingDTO.setStatus("InProgress");

            int bookingId = bookingBO.saveBooking(bookingDTO);


            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setBookingId(bookingId);
            paymentDTO.setAmount(amount);
            paymentDTO.setPaymentMethod("Stripe");
            paymentDTO.setPaymentStatus("Success");
            paymentDTO.setPaymentDate(new Timestamp(new Date().getTime()));

            paymentBO.savePayment(paymentDTO);


            JsonObject responseJson = Json.createObjectBuilder()
                    .add("id", session.getId())
                    .build();


            resp.setContentType("application/json");
            try (JsonWriter jsonWriter = Json.createWriter(resp.getWriter())) {
                jsonWriter.write(responseJson);
            }

        } catch (StripeException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "Failed to create checkout session")
                    .build();
            try (JsonWriter jsonWriter = Json.createWriter(resp.getWriter())) {
                jsonWriter.write(errorJson);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "Invalid amount format")
                    .build();
            try (JsonWriter jsonWriter = Json.createWriter(resp.getWriter())) {
                jsonWriter.write(errorJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "Internal server error")
                    .build();
            try (JsonWriter jsonWriter = Json.createWriter(resp.getWriter())) {
                jsonWriter.write(errorJson);
            }
        }
    }
}