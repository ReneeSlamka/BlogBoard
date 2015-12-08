package com.blogboard.server.web;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger logger = Logger.getLogger(ExceptionControllerAdvice.class.getName());

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An IO exception occurred")
    @ExceptionHandler(IOException.class)
    public void exceptionHandlerIO(IOException ex) {
        logger.log(Level.SEVERE, "An IO exception has occurred, most likely due to a close internet connection", ex);
    }
}
