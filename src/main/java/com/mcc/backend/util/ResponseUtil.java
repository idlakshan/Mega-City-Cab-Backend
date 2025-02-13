package com.mcc.backend.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    public static void sendJsonResponse(HttpServletResponse resp, int status, String message, JsonObject data, String errorMessage) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);

        JsonObjectBuilder responseBuilder = Json.createObjectBuilder()
                .add("status", status)
                .add("message", message)
                .add("data", data != null ? data : Json.createObjectBuilder().build())
                .add("error", Json.createObjectBuilder().add("message", errorMessage != null ? errorMessage : ""));

        resp.getWriter().write(responseBuilder.build().toString());
    }
}
