package pl.kurs.github_info.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import pl.kurs.github_info.client.GithubClient;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.mapper.RepoInfoMapper;
import pl.kurs.github_info.model.RepoInfo;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GithubInfoServiceTest {
    GithubClient client;
    RepoInfoMapper mapper;
    GithubInfoService service;

    @BeforeEach
    void setup() {
        this.client = Mockito.mock(GithubClient.class);
        this.mapper = Mappers.getMapper(RepoInfoMapper.class);
        this.service = new GithubInfoService(client, mapper);
    }

    @Test
    void getRepoInfoByOwnerAndName_DataCorrect_RepoInfoDtoReturned() {
        String owner = "owner";
        String repo = "repo";
        RepoInfo repoInfo = createRepoInfo();
        when(client.getRepoInfoByOwnerAndName(anyString(), anyString())).thenReturn(repoInfo);

        RepoInfoDto repoInfoDto = service.getRepoInfoByOwnerAndName(owner, repo);

        Assertions.assertAll(
                () -> assertEquals("fullName", repoInfoDto.fullName()),
                () -> assertNull(repoInfoDto.description()),
                () -> assertEquals("url", repoInfoDto.cloneUrl()),
                () -> assertEquals(LocalDateTime.of(2015, 8, 15, 20,0,0), repoInfoDto.createdAt()),
                () -> assertEquals(1, repoInfoDto.stars())
        );
    }

    private RepoInfo createRepoInfo() {
        RepoInfo repoInfo = new RepoInfo();
        repoInfo.setFullName("fullName");
        repoInfo.setDescription(null);
        repoInfo.setCloneUrl("url");
        repoInfo.setCreatedAt(LocalDateTime.of(2015, 8,15, 20,0,0));
        repoInfo.setStars(1);
        return repoInfo;
    }
}
