package pl.kurs.github_info.exception;

import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends GithubInfoAppException {
    public RepositoryNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
