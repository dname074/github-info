package pl.kurs.github_info.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.kurs.github_info.client.config.GithubClientConfiguration;
import pl.kurs.github_info.client.config.GithubClientFallback;
import pl.kurs.github_info.model.RepoInfo;

@FeignClient(name = "githubClient",
        url = "https://api.github.com/",
        configuration = GithubClientConfiguration.class,
        fallback = GithubClientFallback.class
)
public interface GithubClient {
    @GetMapping("/repos/{owner}/{repo}")
    RepoInfo getRepoInfoByOwnerAndName(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
