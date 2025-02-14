package com.mcc.backend.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(urlPatterns = "/uploads/*")
public class ImageServlet extends HttpServlet {

    private static final String ROOT_DIR = "D:/Projects/ICBT/Mega City Cab/Backend/uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("wada.....");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 3) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path format");
            return;
        }

        String directory = pathParts[1];
        String filename = pathParts[2];


        File file = new File(ROOT_DIR + File.separator + directory, filename);


        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }


        String mimeType = getServletContext().getMimeType(filename);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        resp.setContentType(mimeType);


        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}