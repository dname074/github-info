package pl.kurs.github_info.client.config;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import pl.kurs.github_info.exception.GithubInfoException;
import pl.kurs.github_info.exception.RepositoryNotFoundException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = FeignException.errorStatus(methodKey, response);
        return switch (response.status()) {
            case 404 -> new RepositoryNotFoundException("Nie znaleziono podanego repozytorium");
            case 500 -> new GithubInfoException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            case 503 -> new RetryableException(response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    exception,
                    100L,
                    response.request()
            );
            default -> exception;
        };
    }
}
