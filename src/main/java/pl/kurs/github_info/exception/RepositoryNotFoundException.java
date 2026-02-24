package pl.kurs.github_info.exception;

import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends GithubInfoException {
    public RepositoryNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
