package pl.kurs.github_info.client.decoder;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import pl.kurs.github_info.exception.RepositoryNotFoundException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = FeignException.errorStatus(methodKey, response);
        if (response.status() == 404) {
            return new RepositoryNotFoundException("Nie znaleziono podanego repozytorium");
        }
        return exception;
    }
}
