package pl.kurs.github_info.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.github_info.client.GithubClient;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.exception.RepositoryNotFoundException;
import pl.kurs.github_info.mapper.RepoInfoMapper;

@Service
@RequiredArgsConstructor
public class GithubInfoService {
    private final GithubClient client;
    private final RepoInfoMapper mapper;

    public RepoInfoDto getRepoInfoByOwnerAndName(String owner, String repository) {
        return mapper.toDto(client.getRepoInfoByOwnerAndName(owner, repository)
                .orElseThrow(() -> new RepositoryNotFoundException("Nie znaleziono podanego repozytorium")));
    }
}
