package pl.kurs.github_info.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kurs.github_info.client.GithubClient;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.exception.RepositoryNotFoundException;
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

    public RepoInfoDto getRepositoryFromLocal(String owner, String repositoryName) {
        RepoInfo repoInfo = repository.findByOwnerAndRepositoryName(owner, repositoryName)
                .orElseThrow(() -> new RepositoryNotFoundException("Nie znaleziono podanego repozytorium"));
        return mapper.toDto(repoInfo);
    }

    public RepoInfoDto saveRepositoryToLocal(String owner, String repositoryName) {
        RepoInfo repoInfo = mapper.toEntity(client.getRepository(owner, repositoryName));
        repository.save(repoInfo);
        return mapper.toDto(repoInfo);
    }

    public RepoInfoDto updateRepositoryFromLocal(String owner, String repositoryName) {
        RepoInfo repoInfo = findRepositoryFromLocal(owner, repositoryName);
        RepoInfo updatedRepoInfo = mapper.toEntity(client.getRepository(owner, repositoryName));
        repoInfo.update(updatedRepoInfo);
        repository.save(repoInfo);
        return mapper.toDto(repoInfo);
    }

    public RepoInfoDto deleteRepositoryFromLocal(String owner, String repositoryName) {
        RepoInfo repoInfo = findRepositoryFromLocal(owner, repositoryName);
        repository.delete(repoInfo);
        return mapper.toDto(repoInfo);
    }

    private RepoInfo findRepositoryFromLocal(String owner, String repositoryName) {
        return repository.findByOwnerAndRepositoryName(owner, repositoryName)
                .orElseThrow(() -> new RepositoryNotFoundException("Nie znaleziono podanego repozytorium w lokalnej bazie danych"));
    }
}
