package com.lvmaizi.easy.ask.service.exception;

import com.lvmaizi.easy.ask.service.response.BaseResponse;
import com.lvmaizi.easy.ask.service.response.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("handleRuntimeException", e);
        return ResponseEntity.ok(ResponseBuilder.fail());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<BaseResponse<Void>> handleServiceException(ServiceException e) {
        log.error("handleServiceException", e);
        return ResponseEntity.ok(ResponseBuilder.fail("服务执行异常"));
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<BaseResponse<Void>> handleServiceException(ClientException e) {
        log.error("handleServiceException", e);
        return ResponseEntity.ok(ResponseBuilder.fail(e.getMessage()));
    }

}
