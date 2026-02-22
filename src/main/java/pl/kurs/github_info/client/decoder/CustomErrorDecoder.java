package pl.kurs.github_info.client.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import pl.kurs.github_info.exception.RepositoryNotFoundException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        if (response.status()==404) {
            return new RepositoryNotFoundException("Nie znaleziono podanego repozytorium");
        }
        return new Exception();
    }
}
