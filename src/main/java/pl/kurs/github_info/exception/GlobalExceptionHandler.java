package pl.kurs.github_info.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.github_info.dto.GithubInfoExceptionDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<GithubInfoExceptionDto> handleFeignException(FeignException exception) {
        return ResponseEntity.status(exception.status()).body(new GithubInfoExceptionDto(exception.getMessage(), HttpStatus.valueOf(exception.status())));
    }
}
