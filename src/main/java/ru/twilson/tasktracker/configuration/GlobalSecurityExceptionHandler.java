package ru.twilson.tasktracker.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.twilson.tasktracker.model.Error;

@ControllerAdvice
public class GlobalSecurityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return new ResponseEntity<>(new Error("Access Denied", ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(
            AuthenticationException ex) {

        return new ResponseEntity<>(new Error("Authentication failed", ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }
}

