package pl.kurs.github_info.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GithubInfoException extends RuntimeException {
    private final HttpStatus status;

    public GithubInfoException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
