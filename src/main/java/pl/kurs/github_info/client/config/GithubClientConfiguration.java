package pl.kurs.github_info.client.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import pl.kurs.github_info.client.decoder.CustomErrorDecoder;

public class GithubClientConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
