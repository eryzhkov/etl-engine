package etl.engine.ems.controller;

import etl.engine.ems.model.ResponseSingleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ResponseSingleDto<Object>> handler(Exception ex, WebRequest request) {
      log.debug("Got exception: request='{}', error='{}'", request, ex.getMessage());
      log.error("{}", ex.getMessage(), ex);
      final ResponseSingleDto<Object> body = new ResponseSingleDto<>(
              "error",
              OffsetDateTime.now(),
              ex.getMessage(),
              null);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .contentType(MediaType.APPLICATION_JSON)
              .cacheControl(CacheControl.noCache())
              .body(body);
    }

}
