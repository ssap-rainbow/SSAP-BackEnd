package ssap.ssap.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomDuplicateKeyException.class)
    public ResponseEntity<?> handleCustomDuplicateKeyException(CustomDuplicateKeyException ex) {
        String errorMessage = "해당 제공자 ID는 이미 사용 중입니다: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomServiceException.class)
    public ResponseEntity<?> handleCustomServiceException(CustomServiceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessageBuilder = new StringBuilder();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            if (errorMessageBuilder.length() > 0) {
                errorMessageBuilder.append(", ");
            }
            errorMessageBuilder.append(fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errorMessageBuilder.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<?> handleInvalidAccessTokenException(InvalidAccessTokenException ex) {
        log.error("유효하지 않은 액세스 토큰 예외: " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        String message = name + " 파라미터가 누락되었습니다";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

}