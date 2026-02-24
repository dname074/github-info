package pl.kurs.github_info;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GithubInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubInfoApplication.class, args);
    }

}
