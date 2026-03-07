package pl.kurs.github_info.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.github_info.dto.ErrorMessageDto;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(GithubInfoException.class)
    public ResponseEntity<ErrorMessageDto> handleFeignException(GithubInfoException exception) {
        log.error("Exception with status {} occured: {}", exception.getStatus(), exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(new ErrorMessageDto(exception.getMessage(), exception.getStatus()));
    }
}
