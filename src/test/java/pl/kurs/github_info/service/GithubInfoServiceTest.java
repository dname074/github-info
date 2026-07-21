package pl.kurs.github_info.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import pl.kurs.github_info.argumentmatcher.RepoArgumentMatcher;
import pl.kurs.github_info.client.GithubClient;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.exception.RepositoryNotFoundException;
import pl.kurs.github_info.mapper.RepoInfoMapper;
import pl.kurs.github_info.model.RepoInfo;
import pl.kurs.github_info.repository.GithubInfoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GithubInfoServiceTest {
    GithubClient client;
    RepoInfoMapper mapper;
    GithubInfoService service;
    GithubInfoRepository repository;

    @BeforeEach
    void setup() {
        this.client = Mockito.mock(GithubClient.class);
        this.repository = Mockito.mock(GithubInfoRepository.class);
        this.mapper = Mappers.getMapper(RepoInfoMapper.class);
        this.service = new GithubInfoService(client, mapper, repository);
    }

    @Test
    void getRepository_DataCorrect_RepoInfoDtoReturned() {
        String owner = "owner";
        String repo = "repo";
        RepoInfoDto repoInfo = createRepoInfo();
        when(client.getRepository(anyString(), anyString())).thenReturn(repoInfo);

        RepoInfoDto repoInfoDto = service.getRepository(owner, repo);

        Assertions.assertAll(
                () -> assertEquals("fullName", repoInfoDto.fullName()),
                () -> assertNull(repoInfoDto.description()),
                () -> assertEquals("url", repoInfoDto.cloneUrl()),
                () -> assertEquals(LocalDateTime.of(2015, 8, 15, 20,0,0), repoInfoDto.createdAt()),
                () -> assertEquals(1, repoInfoDto.stars())
        );
        verify(client,times(1)).getRepository("owner", "repo");
        verifyNoMoreInteractions(client);
        verifyNoInteractions(repository);
    }

    @Test
    void getRepository_RepoNotFound_RepositoryNotFoundExceptionThrown() {
        String owner = "owner";
        String repo = "repo";
        when(client.getRepository(anyString(), anyString())).thenThrow(new RepositoryNotFoundException("Nie znaleziono podanego repozytorium"));

        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class, () -> service.getRepository(owner, repo));
        assertEquals("Nie znaleziono podanego repozytorium", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(client, times(1)).getRepository("owner", "repo");
        verifyNoMoreInteractions(client);
        verifyNoInteractions(repository);
    }

    @Test
    void getRepositoryFromLocal_RepositoryFound_RepoInfoDtoReturned() {
        String owner = "owner";
        String repoName = "repoName";
        RepoInfo repoInfo = mapper.toEntity(createRepoInfo());
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.of(repoInfo));
        RepoInfoDto result = service.getRepositoryFromLocal(owner, repoName);
        Assertions.assertAll(
                () -> assertEquals("fullName", result.fullName()),
                () -> assertNull(result.description()),
                () -> assertEquals("url", result.cloneUrl()),
                () -> assertEquals(1, result.stars()),
                () -> assertEquals(LocalDateTime.of(2015,8,15,20,0,0), result.createdAt())
        );
        verify(repository, times(1)).findByOwnerAndRepositoryName("owner", "repoName");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(client);
    }

    @Test
    void getRepositoryFromLocal_RepositoryNotFound_RepositoryNotFoundExceptionThrown() {
        String owner = "owner";
        String repoName = "repoName";
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.empty());
        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class, () -> service.getRepositoryFromLocal(owner, repoName));
        assertEquals("Nie znaleziono podanego repozytorium w lokalnej bazie danych", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repository, times(1)).findByOwnerAndRepositoryName("owner", "repoName");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(client);
    }

    @Test
    void saveRepositoryToLocal_RepositoryFound_RepoInfoDtoReturned() {
        String owner = "owner";
        String repoName = "repoName";
        RepoInfoDto repoInfoDto = createRepoInfo();
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        when(client.getRepository(owner, repoName)).thenReturn(repoInfoDto);
        when(repository.save(any())).thenReturn(repoInfo);
        RepoInfoDto result = service.saveRepositoryToLocal(owner, repoName);
        Assertions.assertAll(
                () -> assertEquals("fullName", result.fullName()),
                () -> assertNull(result.description()),
                () -> assertEquals("url", result.cloneUrl()),
                () -> assertEquals(1, result.stars()),
                () -> assertEquals(LocalDateTime.of(2015,8,15,20,0,0), result.createdAt())
        );
        verify(repository, times(1)).save(argThat(new RepoArgumentMatcher(repoInfo)));
        verify(client, times(1)).getRepository("owner", "repoName");
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(client);
    }

    @Test
    void saveRepositoryToLocal_EmptyRepositoryReceived_EmptyRepoInfoDtoReturned() {
        String owner = "owner";
        String repoName = "repoName";
        RepoInfoDto repoInfoDto = new RepoInfoDto(null, null, null, null, null);
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        when(client.getRepository(owner, repoName)).thenReturn(repoInfoDto);
        RepoInfoDto result = service.saveRepositoryToLocal(owner, repoName);
        Assertions.assertAll(
                () -> assertNull(result.fullName()),
                () -> assertNull(result.description()),
                () -> assertNull(result.cloneUrl()),
                () -> assertNull(result.stars()),
                () -> assertNull(result.createdAt())
        );
        verify(client, times(1)).getRepository("owner", "repoName");
        verifyNoInteractions(repository);
        verifyNoMoreInteractions(client);
    }

    @Test
    void updateRepositoryFromLocal_RepositoryFound_RepositoryUpdatedAndRepoInfoDtoReturned() {
        String owner = "owner";
        String repoName = "repoName";
        RepoInfoDto repoInfoDto = createRepoInfo();
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        RepoInfoDto updatedRepoInfoDto = new RepoInfoDto("fullName", "description", "url", 1,
                LocalDateTime.of(2015, 8,15, 20,0,0));
        RepoInfo updatedRepoInfo = mapper.toEntity(updatedRepoInfoDto);
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.of(repoInfo));
        when(client.getRepository(owner, repoName)).thenReturn(updatedRepoInfoDto);
        when(repository.save(any())).thenReturn(repoInfo);
        RepoInfoDto result = service.updateRepositoryFromLocal(owner, repoName);
        Assertions.assertAll(
                () -> assertEquals("fullName", result.fullName()),
                () -> assertEquals("description", result.description()),
                () -> assertEquals("url", result.cloneUrl()),
                () -> assertEquals(1, result.stars()),
                () -> assertEquals(LocalDateTime.of(2015,8,15,20,0,0), result.createdAt())
        );
        verify(repository, times(1)).findByOwnerAndRepositoryName("owner", "repoName");
        verify(client, times(1)).getRepository("owner", "repoName");
        verify(repository, times(1)).save(argThat(new RepoArgumentMatcher(updatedRepoInfo)));
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(client);
    }

    @Test
    void updateRepositoryFromLocal_RepositoryNotFoundInLocal_RepositoryNotFoundReturned() {
        String owner = "owner";
        String repoName = "repoName";
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.empty());
        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class, () -> service.updateRepositoryFromLocal(owner, repoName));
        assertEquals("Nie znaleziono podanego repozytorium w lokalnej bazie danych", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repository, times(1)).findByOwnerAndRepositoryName("owner", "repoName");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(client);
    }

    @Test
    void updateRepositoryFromLocal_RepositoryNotFoundInRemote_EmptyRepoInfoDtoReturned() {
        String owner = "owner";
        String repoName = "repoName";
        RepoInfoDto repoInfoDto = createRepoInfo();
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        RepoInfoDto emptyRepoInfoDto = new RepoInfoDto(null, null, null, null, null);
        RepoInfo emptyRepoInfo = mapper.toEntity(emptyRepoInfoDto);
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.of(repoInfo));
        when(client.getRepository(owner, repoName)).thenReturn(emptyRepoInfoDto);
        when(repository.save(any())).thenReturn(emptyRepoInfo);
        RepoInfoDto result = service.updateRepositoryFromLocal(owner, repoName);
        Assertions.assertAll(
                () -> assertNull(result.fullName()),
                () -> assertNull(result.description()),
                () -> assertNull(result.cloneUrl()),
                () -> assertNull(result.stars()),
                () -> assertNull(result.createdAt())
        );
        verify(repository, times(1)).findByOwnerAndRepositoryName("owner", "repoName");
        verify(client, times(1)).getRepository("owner", "repoName");
        verify(repository, times(1)).save(argThat(new RepoArgumentMatcher(emptyRepoInfo)));
        verifyNoMoreInteractions(client);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteRepositoryFromLocal_RepositoryFound_RepositoryDeletedAndRepoInfoDtoReturned() {
        String owner = "owner";
        String repoName = "repoName";
        RepoInfoDto repoInfoDto = createRepoInfo();
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.of(repoInfo));
        doNothing().when(repository).delete(any());
        RepoInfoDto result = service.deleteRepositoryFromLocal(owner, repoName);
        Assertions.assertAll(
                () -> assertEquals("fullName", result.fullName()),
                () -> assertNull(result.description()),
                () -> assertEquals("url", result.cloneUrl()),
                () -> assertEquals(1, result.stars()),
                () -> assertEquals(LocalDateTime.of(2015,8,15,20,0,0), result.createdAt())
        );
        verify(repository, times(1)).findByOwnerAndRepositoryName("owner", "repoName");
        verify(repository, times(1)).delete(repoInfo);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(client);
    }

    @Test
    void deleteRepositoryFromLocal_RepositoryNotFound_RepositoryNotFoundExceptionThrown() {
        String owner = "owner";
        String repoName = "repoName";
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.empty());
        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class, () -> service.deleteRepositoryFromLocal(owner, repoName));
        assertEquals("Nie znaleziono podanego repozytorium w lokalnej bazie danych", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repository, times(1)).findByOwnerAndRepositoryName("owner", "repoName");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(client);
    }

    private RepoInfoDto createRepoInfo() {
        return new RepoInfoDto(
                "fullName", null, "url", 1,
                LocalDateTime.of(2015, 8,15, 20,0,0));
    }
}
