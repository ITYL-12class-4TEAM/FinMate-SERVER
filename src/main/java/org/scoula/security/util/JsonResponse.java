package org.scoula.security.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class JsonResponse {
    public static <T> void send(HttpServletResponse response, T result) throws IOException {
        ObjectMapper om = new ObjectMapper();

        response.setContentType("application/json;charset=UTF-8");
        Writer out = response.getWriter();
        out.write(om.writeValueAsString(result));
        out.flush();
    }

    public static void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        Writer out = response.getWriter();
        out.write(message);
        out.flush();
    }
    public static void sendError(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        response.setStatus(responseCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        // ApiResponse 형태로 일관된 응답
        ApiResponse<Object> apiResponse = ApiResponse.fail(responseCode);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Writer out = response.getWriter();
        out.write(mapper.writeValueAsString(apiResponse));
        out.flush();
    }
}

