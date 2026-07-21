package pl.kurs.github_info.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.mapper.RepoInfoMapper;
import pl.kurs.github_info.model.RepoInfo;
import pl.kurs.github_info.repository.GithubInfoRepository;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWireMock(port = 8081)
@AutoConfigureMockMvc
@Transactional
public class GithubInfoControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WireMockServer githubClientMock;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RepoInfoMapper mapper;
    @Autowired
    GithubInfoRepository repository;

    @BeforeEach
    void setup() {
        githubClientMock.resetAll();
    }

    @Test
    void getRepository_CorrectDataPassed_RepoInfoDtoReturned() throws Exception {
        RepoInfoDto repoInfoDto = new RepoInfoDto("owner/repoName", "description", "cloneUrl", 1,
                LocalDateTime.of(2015, 10, 25, 20, 0, 0));
        githubClientMock.stubFor(WireMock.get("/repos/owner/repoName").willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(repoInfoDto))
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("owner/repoName"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.clone_url").value("cloneUrl"))
                .andExpect(jsonPath("$.stargazers_count").value(1))
                .andExpect(jsonPath("$.created_at").value("2015-10-25T20:00:00"));
        verify(1, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
    }

    @Test
    void getRepository_InvalidDataPassed_EmptyRepoInfoDtoReturned() throws Exception {
        githubClientMock.stubFor(WireMock.get("/repos/owner/repoName").willReturn(
                aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").isEmpty())
                .andExpect(jsonPath("$.description").isEmpty())
                .andExpect(jsonPath("$.clone_url").isEmpty())
                .andExpect(jsonPath("$.stargazers_count").isEmpty())
                .andExpect(jsonPath("$.created_at").isEmpty());
        verify(1, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
    }

    @Test
    void getRepositoryFromLocal_CorrectDataPassed_RepoInfoDtoReturned() throws Exception {
        RepoInfoDto repoInfoDto = new RepoInfoDto("owner/repoName", "description", "cloneUrl", 1,
                LocalDateTime.of(2015, 10, 25, 20, 0, 0));
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        repository.save(repoInfo);

        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/local/owner/repoName"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("owner/repoName"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.clone_url").value("cloneUrl"))
                .andExpect(jsonPath("$.stargazers_count").value(1))
                .andExpect(jsonPath("$.created_at").value("2015-10-25T20:00:00"));
        assertTrue(repository.findByOwnerAndRepositoryName("owner", "repoName").isPresent());
    }

    @Test
    void getRepositoryFromLocal_InvalidRepositoryDataPassed_404Returned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/local/owner/repoName"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono podanego repozytorium w lokalnej bazie danych"));
        assertTrue(repository.findByOwnerAndRepositoryName("owner", "repoName").isEmpty());
    }

    @Test
    void saveRepositoryToLocal_InvalidRepositoryDataPassed_EmptyRepoInfoDtoReturned() throws Exception {
        githubClientMock.stubFor(WireMock.get("/repos/owner/repoName").willReturn(
                aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.full_name").isEmpty())
                .andExpect(jsonPath("$.description").isEmpty())
                .andExpect(jsonPath("$.clone_url").isEmpty())
                .andExpect(jsonPath("$.stargazers_count").isEmpty())
                .andExpect(jsonPath("$.created_at").isEmpty());
        verify(1, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
        assertTrue(repository.findByOwnerAndRepositoryName("owner", "repoName").isEmpty());
    }

    @Test
    void saveRepositoryToLocal_DataCorrect_RepoInfoDtoReturned() throws Exception {
        RepoInfoDto repoInfoDto = new RepoInfoDto("owner/repoName", "description", "cloneUrl", 1,
                LocalDateTime.of(2015, 10, 25, 20, 0, 0));
        githubClientMock.stubFor(WireMock.get("/repos/owner/repoName").willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(repoInfoDto))
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.full_name").value("owner/repoName"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.clone_url").value("cloneUrl"))
                .andExpect(jsonPath("$.stargazers_count").value(1))
                .andExpect(jsonPath("$.created_at").value("2015-10-25T20:00:00"));
        verify(1, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
        RepoInfo result = repository.findByOwnerAndRepositoryName("owner", "repoName")
                .orElseGet(() -> new RepoInfo(null, null, null, null, null, null));
        Assertions.assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals("owner/repoName", result.getFullName()),
                () -> assertEquals("description", result.getDescription()),
                () -> assertEquals("cloneUrl", result.getCloneUrl()),
                () -> assertEquals(1, result.getStars()),
                () -> assertEquals(LocalDateTime.of(2015, 10, 25, 20, 0, 0), result.getCreatedAt())
        );
    }

    @Test
    void updateRepositoryFromLocal_DataCorrect_RepoInfoDtoReturned() throws Exception {
        RepoInfoDto repoInfoDto = new RepoInfoDto("owner/repoName", "description", "cloneUrl", 1,
                LocalDateTime.of(2015, 10, 25, 20, 0, 0));
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        RepoInfoDto updatedRepoInfoDto = new RepoInfoDto("owner/repoName", "description2", "cloneUrl3", 1,
                LocalDateTime.of(2015, 10, 25, 20, 0, 0));
        githubClientMock.stubFor(WireMock.get("/repos/owner/repoName").willReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(updatedRepoInfoDto))
        ));
        repository.save(repoInfo);

        mockMvc.perform(MockMvcRequestBuilders.put("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("owner/repoName"))
                .andExpect(jsonPath("$.description").value("description2"))
                .andExpect(jsonPath("$.clone_url").value("cloneUrl3"))
                .andExpect(jsonPath("$.stargazers_count").value(1))
                .andExpect(jsonPath("$.created_at").value("2015-10-25T20:00:00"));
        verify(1, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
        RepoInfo result = repository.findByOwnerAndRepositoryName("owner", "repoName")
                        .orElseGet(() -> new RepoInfo(null, null, null, null, null, null));
        Assertions.assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals("owner/repoName", result.getFullName()),
                () -> assertEquals("description2", result.getDescription()),
                () -> assertEquals("cloneUrl3", result.getCloneUrl()),
                () -> assertEquals(1, result.getStars()),
                () -> assertEquals(LocalDateTime.of(2015, 10, 25, 20, 0, 0), result.getCreatedAt())
        );
    }

    @Test
    void deleteRepositoryFromLocal_DataCorrect_RepoInfoDtoReturned() throws Exception {
        RepoInfoDto repoInfoDto = new RepoInfoDto("owner/repoName", "description", "cloneUrl", 1,
                LocalDateTime.of(2015, 10, 25, 20, 0, 0));
        RepoInfo repoInfo = mapper.toEntity(repoInfoDto);
        repository.save(repoInfo);

        mockMvc.perform(MockMvcRequestBuilders.delete("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("owner/repoName"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.clone_url").value("cloneUrl"))
                .andExpect(jsonPath("$.stargazers_count").value(1))
                .andExpect(jsonPath("$.created_at").value("2015-10-25T20:00:00"));
        assertTrue(repository.findByOwnerAndRepositoryName("owner", "repoName").isEmpty());
    }
}
