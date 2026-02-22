package pl.kurs.github_info.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.github_info.dto.GithubInfoExceptionDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GithubInfoAppException.class)
    public ResponseEntity<GithubInfoExceptionDto> handleFeignException(GithubInfoAppException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new GithubInfoExceptionDto(exception.getMessage(), exception.getStatus()));
    }
}
