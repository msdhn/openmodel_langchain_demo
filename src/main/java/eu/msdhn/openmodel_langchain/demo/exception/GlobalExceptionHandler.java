package eu.msdhn.openmodel_langchain.demo.exception;

import eu.msdhn.openmodel_langchain.demo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AssistantException.class)
    public ResponseEntity<ErrorResponse> handleAssistantException(AssistantException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("ASSISTANT_ERROR", exception.getMessage()));
    }
}
