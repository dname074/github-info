package pl.kurs.github_info.client.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubClientConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);
    }
}
