package pl.kurs.github_info.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.kurs.github_info.client.config.GithubClientConfiguration;
import pl.kurs.github_info.model.RepoInfo;

@FeignClient(name = "GithubClient", url = "https://api.github.com/", configuration = GithubClientConfiguration.class)
public interface GithubClient {
    @RequestMapping(method = RequestMethod.GET, value = "/repos/{owner}/{repo}")
    RepoInfo getRepoInfoByOwnerAndName(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
