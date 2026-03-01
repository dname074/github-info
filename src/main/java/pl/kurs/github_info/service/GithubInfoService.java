package pl.kurs.github_info.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kurs.github_info.client.GithubClient;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.mapper.RepoInfoMapper;
import pl.kurs.github_info.model.RepoInfo;
import pl.kurs.github_info.repository.GithubInfoRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubInfoService {
    private final GithubClient client;
    private final RepoInfoMapper mapper;
    private final GithubInfoRepository repository;

    public RepoInfoDto getRepository(String owner, String repositoryName) {
        log.info("Process of getting informations about github repository started");
        RepoInfoDto repoInfoDto = client.getRepository(owner, repositoryName);
        log.info("Process of getting informations about github repository ended");
        return repoInfoDto;
    }

    public RepoInfoDto saveRepositoryToLocal(String owner, String repositoryName) {
        RepoInfo repoInfo = mapper.toEntity(client.getRepository(owner, repositoryName));
        repository.save(repoInfo);
        return mapper.toDto(repoInfo);
    }
}
