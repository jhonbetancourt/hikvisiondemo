package com.infomedia.hikvisiondemo.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.infomedia.hikvisiondemo.dto.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@ControllerAdvice
@Configuration
public class ExceptionHandlerConfig extends ResponseEntityExceptionHandler {
    private static final String INTERNAL_ERROR_MSG = "Internal error";
    private static final String NORMAL_ERROR_MSG = "Error";


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
        log.error(NORMAL_ERROR_MSG, ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status, request, ex
                , ex.getName()+" parameter type mismatch"), status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex
            , @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        log.error(NORMAL_ERROR_MSG, ex);
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName+" "+errorMessage);
        });

        String errorsString = errors.toString();
        String msg = errorsString.substring(1, errorsString.length()-1);

        return new ResponseEntity<>(new ErrorResponse(status, request, ex, msg), status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> constraintViolationException(
            HttpServletRequest request, ConstraintViolationException ex) {
        log.error(NORMAL_ERROR_MSG, ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(constraintViolation ->
            errors.add(constraintViolation.getPropertyPath().toString()+" "+constraintViolation.getMessage())
        );

        String errorsString = errors.toString();
        String msg = errorsString.substring(1, errorsString.length()-1);

        return new ResponseEntity<>(new ErrorResponse(status, request
                , MethodArgumentNotValidException.class.getSimpleName(), msg), status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex
            , @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        log.error(NORMAL_ERROR_MSG, ex);
        return new ResponseEntity<>(new ErrorResponse(status, request, ex
                , "Parameter with name "+ex.getParameterName()+" is missing"), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        log.error(NORMAL_ERROR_MSG, ex);

        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if(rootCause instanceof InvalidFormatException){
            InvalidFormatException invalidFormatException = (InvalidFormatException) rootCause;
            return new ResponseEntity<>(new ErrorResponse(status, request, ex
                    , invalidFormatException.getTargetType().getSimpleName()+" "
                    +invalidFormatException.getValue().toString()+" has an incorrect format"), status);
        }

        if(rootCause instanceof DateTimeParseException){
            InvalidFormatException invalidFormatException = (InvalidFormatException)
                    ExceptionUtils.getThrowableList(ex).get(ExceptionUtils.indexOfType(ex, InvalidFormatException.class));

            return new ResponseEntity<>(new ErrorResponse(status, request, ex
                    , invalidFormatException.getValue().toString()+ " has an incorrect datetime format"), status);
        }

        if(rootCause instanceof UnrecognizedPropertyException){
            UnrecognizedPropertyException unrecognizedPropertyException = (UnrecognizedPropertyException) rootCause;

            return new ResponseEntity<>(new ErrorResponse(status, request, ex
                    , "Unrecognized field "+unrecognizedPropertyException.getPropertyName()), status);
        }

        if(rootCause instanceof JsonParseException){
            JsonParseException jsonParseException = (JsonParseException) rootCause;
            String msg = jsonParseException.getMessage();
            if(msg.contains("\n at")){
                msg = jsonParseException.getMessage().split("\n at")[0];
            }
            return new ResponseEntity<>(new ErrorResponse(status, request, ex, msg), status);
        }

        return new ResponseEntity<>(new ErrorResponse(status, request, rootCause, rootCause.getMessage()), status);
    }
}