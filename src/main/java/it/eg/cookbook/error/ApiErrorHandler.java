package it.eg.cookbook.error;

import it.eg.cookbook.model.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ApiErrorHandler extends ResponseEntityExceptionHandler {

    private static final String EXCEPTION_MESSAGE_TRAILER = "An exception occurred! {}";

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ResponseMessage> handleBusinessException(final BusinessException ex, final WebRequest request) {
        if (ex.getCause() != null) {
            log.error(EXCEPTION_MESSAGE_TRAILER, ex.getResponseCode().name() + " - " + ex.getResponseCode().getMessage(), ex);
        } else {
            log.error(EXCEPTION_MESSAGE_TRAILER, ex.getResponseCode().name() + " - " + ex.getResponseCode().getMessage());
        }

        return new ResponseEntity<>(new ResponseMessage(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseMessage> handleGenericException(final Exception ex, final WebRequest request) {
        log.error(EXCEPTION_MESSAGE_TRAILER, ex.getMessage(), ex);

        return new ResponseEntity<>(new ResponseMessage(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}