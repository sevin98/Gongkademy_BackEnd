package com.gongkademy.global.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e){
        log.info("message: "+ e.getErrorCode().getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(Map.of("message",e.getErrorCode().getMessage()));
    }
}
