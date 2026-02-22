package pl.kurs.github_info.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.service.GithubInfoService;

@RequestMapping("/repositories")
@RequiredArgsConstructor
@RestController
@Slf4j
public class GithubInfoController {
    private final GithubInfoService service;

    @GetMapping("/{owner}/{repo}")
    public RepoInfoDto getRepoInfoByOwnerAndName(@PathVariable String owner, @PathVariable String repo) {
        log.info("Received GET /repositories/{}/{} request", owner, repo);
        return service.getRepoInfoByOwnerAndName(owner, repo);
    }
}
