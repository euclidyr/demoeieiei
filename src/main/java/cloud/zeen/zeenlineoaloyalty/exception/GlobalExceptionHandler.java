package cloud.zeen.zeenlineoaloyalty.exception;

import cloud.zeen.zeenlineoaloyalty.domain.core.ResponseBodyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBodyTemplate<Object>> handleGeneralException(Exception e) {
        log.error("Internal server error: {}", e.getMessage(), e);

        ResponseBodyTemplate<Object> response = new ResponseBodyTemplate<>();
        response.setOperationError(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred. Please contact support.", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseBodyTemplate<Object>> handleBadRequest(ApiException e) {
        ResponseBodyTemplate<Object> response = new ResponseBodyTemplate<>();
        HttpStatus status = e.getStatus();
        response.setOperationError(status, e.getMessage(), null);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ResponseBodyTemplate<Object>> handleHttpClientError(HttpClientErrorException e) {
        ResponseBodyTemplate<Object> response = new ResponseBodyTemplate<>();
        HttpStatus status = e.getStatusCode();
        response.setOperationError(status, "An error occurred while processing your HTTP request.", null);
        return new ResponseEntity<>(response, status);
    }
}
