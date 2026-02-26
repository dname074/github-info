package pl.kurs.github_info.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.kurs.github_info.client.GithubClient;
import pl.kurs.github_info.model.RepoInfo;

@Component
@Slf4j
public class GithubClientFallback implements GithubClient {
    @Override
    public RepoInfo getRepository(String owner, String repo) {
        log.info("Fallback occured");
        return new RepoInfo();
    }
}
