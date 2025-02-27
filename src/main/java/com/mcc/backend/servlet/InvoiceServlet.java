package com.mcc.backend.servlet;

import com.mcc.backend.bo.BOFactory;
import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.bo.custom.PaymentBO;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.dto.PaymentDTO;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

@WebServlet("/generate-invoice")
public class InvoiceServlet extends HttpServlet {
    @Resource(name = "java:comp/env/db/pool")
    public static DataSource dataSource;

    private final CarBO carBO = (CarBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CAR);
    private final DriverBO driverBO = (DriverBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.DRIVER);
    private final BookingBO bookingBO = (BookingBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.BOOKING);
    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PAYMENT);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String bookingId = req.getParameter("bookingId");

        if (bookingId == null || bookingId.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
            return;
        }

        try {

            BookingDTO booking = bookingBO.getBookingById(Integer.parseInt(bookingId));
            CarDTO car=carBO.getBookedVehicleById(booking.getCarId());
            DriverDTO driver=driverBO.getBookedDriverById(booking.getDriverId());
            PaymentDTO payment=paymentBO.getPaymentByBookingId(Integer.parseInt(bookingId));


            if (booking == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                return;
            }


            String htmlTemplate = loadHtmlTemplate("invoice-template.html");
            int receiptNumber = (int) (Math.random() * 1000000);
            String receiptDate = LocalDate.now().toString();
            String receiptTime = LocalTime.now().toString();

            htmlTemplate = htmlTemplate.replace("${bookingId}", bookingId)
                    .replace("${customerName}", booking.getCustomerName())
                    .replace("${pickupLocation}", booking.getPickupLocation())
                    .replace("${dropLocation}", booking.getDropLocation())
                    .replace("${bookingDate}", booking.getBookingDateTime().toString())
                    .replace("${driverName}", driver.getDriverName())
                    .replace("${driverContact}", driver.getDriverContact())
                    .replace("${carName}", car.getCarName())
                    .replace("${carNumber}", car.getCarNumber())
                    .replace("${paymentDate}", payment.getPaymentDate().toString())
                    .replace("${amount}",Double.toString(payment.getAmount()))
                    .replace("${receiptNumber}", String.valueOf(receiptNumber))
                    .replace("${receiptDate}", receiptDate)
                    .replace("${receiptTime}", receiptTime);



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