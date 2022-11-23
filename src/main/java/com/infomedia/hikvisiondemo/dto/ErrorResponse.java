package com.infomedia.hikvisiondemo.dto;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private Integer status;
    private String error;
    private String path;
    private String exception;
    private String message;

    public ErrorResponse(@NonNull HttpStatus httpStatus, @NonNull HttpServletRequest httpServletRequest, @NonNull Throwable e, String message) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.path = httpServletRequest.getServletPath();
        this.exception = e.getClass().getSimpleName();
        this.message = message;
    }

    public ErrorResponse(@NonNull HttpStatus httpStatus, @NonNull WebRequest request, @NonNull Throwable e, String message) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
        this.exception = e.getClass().getSimpleName();
        this.message = message;
    }

    public ErrorResponse(@NonNull HttpStatus httpStatus, @NonNull String path) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.path = path;
    }

    public ErrorResponse(@NonNull HttpStatus httpStatus, @NonNull HttpServletRequest httpServletRequest, @NonNull String exceptionName, String message) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.path = httpServletRequest.getRequestURI();
        this.exception = exceptionName;
        this.message = message;
    }

    public static final String KEY_STATUS = "status";
    public static final String KEY_ERROR = "error";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_PATH = "path";
    private static final String KEY_EXCEPTION = "exception";

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_STATUS, status);
        map.put(KEY_ERROR, error);
        map.put(KEY_PATH, path);
        map.put(KEY_EXCEPTION, exception);
        map.put(KEY_MESSAGE, message);
        return map;
    }
}