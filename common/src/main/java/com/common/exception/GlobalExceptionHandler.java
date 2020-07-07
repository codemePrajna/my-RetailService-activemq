package com.common.exception;

import com.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    Map<String, HttpStatus> exceptionToResponseMap = new HashMap<String, HttpStatus>() {
        {
            put(HttpRequestMethodNotSupportedException.class.getName(), HttpStatus.METHOD_NOT_ALLOWED);
            put(RuntimeException.class.getName(), HttpStatus.INTERNAL_SERVER_ERROR);
            put(Exception.class.getName(), HttpStatus.INTERNAL_SERVER_ERROR);
            put(HttpStatusCodeException.class.getName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    };

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> globalExceptionHandler(Exception ex, WebRequest request) throws Exception {
        HttpStatus status = this.exceptionToResponseMap.get(ex.getClass().getName());
        if (status == null) {
            throw ex;
        }
        return problem(ex, status);
    }

    private ResponseEntity<Response> problem(Exception ex, HttpStatus status) {
        return new ResponseEntity<Response>(
                new Response<>()
                        .setStatus(status.value())
                        .setMessage(status.getReasonPhrase())
                        .setError(ex.getMessage()), status
        );
    }
}

