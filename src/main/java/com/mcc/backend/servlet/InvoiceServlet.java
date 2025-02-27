package com.mcc.backend.servlet;

import com.mcc.backend.bo.BOFactory;
import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.dto.BookingDTO;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@WebServlet("/generate-invoice")
public class InvoiceServlet extends HttpServlet {
    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private final BookingBO bookingBO = (BookingBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.BOOKING);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String bookingId = req.getParameter("bookingId");

        if (bookingId == null || bookingId.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
            return;
        }

        try {
            // Fetch booking details from the database using bookingId
            BookingDTO booking = bookingBO.getBookingById(Integer.parseInt(bookingId));

            if (booking == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                return;
            }

            // Load HTML template
            String htmlTemplate = loadHtmlTemplate("invoice-template.html");

            // Replace placeholders with actual data
            htmlTemplate = htmlTemplate.replace("${bookingId}", bookingId)
                    .replace("${customerName}", booking.getCustomerName())
                    .replace("${pickupLocation}", booking.getPickupLocation())
                    .replace("${dropLocation}", booking.getDropLocation())
                    .replace("${bookingDate}", booking.getBookingDateTime().toString())
                    .replace("${amount}", "LKR " + 1200)
                    .replace("${paymentMethod}", "Stripe")
                    .replace("${paymentStatus}", "Success");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlTemplate);
            renderer.layout();
            renderer.createPDF(baos);
            baos.close();

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=invoice-" + bookingId + ".pdf");


            resp.getOutputStream().write(baos.toByteArray());
            resp.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while generating the invoice");
        }
    }

    private String loadHtmlTemplate(String templateName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templateName);

             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }
}