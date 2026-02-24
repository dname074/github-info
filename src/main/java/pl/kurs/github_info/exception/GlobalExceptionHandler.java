package pl.kurs.github_info.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.github_info.dto.ErrorMessageDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GithubInfoException.class)
    public ResponseEntity<ErrorMessageDto> handleFeignException(GithubInfoException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new ErrorMessageDto(exception.getMessage(), exception.getStatus()));
    }
}
