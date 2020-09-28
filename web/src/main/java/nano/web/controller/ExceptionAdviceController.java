package nano.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdviceController {

    private static final Logger log = LoggerFactory.getLogger(ExceptionAdviceController.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        var cause = NestedExceptionUtils.getMostSpecificCause(ex);
        if (log.isDebugEnabled()) {
            log.debug(cause.getMessage(), cause);
        }
        var exClass = cause.getClass();
        var message = cause.getMessage();
        if (message == null) {
            message = exClass.getName();
        }
        var status = HttpStatus.OK;
        if (exClass.isAnnotationPresent(ResponseStatus.class)) {
            var responseStatus = exClass.getAnnotation(ResponseStatus.class);
            status = responseStatus.value();
        }
        return ResponseEntity.status(status).body(Result.error(message));
    }
}
