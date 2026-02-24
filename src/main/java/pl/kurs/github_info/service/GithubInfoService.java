package pl.kurs.github_info.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kurs.github_info.client.GithubClient;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.mapper.RepoInfoMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubInfoService {
    private final GithubClient client;
    private final RepoInfoMapper mapper;

    public RepoInfoDto getRepository(String owner, String repository) {
        log.info("Process of getting informations about github repository started");
        RepoInfoDto repoInfoDto = mapper.toDto(client.getRepoInfoByOwnerAndName(owner, repository));
        log.info("Process of getting informations about github repository ended");
        return repoInfoDto;
    }
}
