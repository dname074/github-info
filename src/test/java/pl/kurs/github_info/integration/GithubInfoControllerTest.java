package pl.kurs.github_info.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWireMock(port = 8081)
@AutoConfigureMockMvc
public class GithubInfoControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WireMockServer githubClientMock;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RepoInfoMapper mapper;

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
                .andExpect(jsonPath("$.fullName").value("owner/repoName"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.cloneUrl").value("cloneUrl"))
                .andExpect(jsonPath("$.stars").value(1))
                .andExpect(jsonPath("$.createdAt").value("2015-10-25T20:00:00"));
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
                .andExpect(jsonPath("$.fullName").isEmpty())
                .andExpect(jsonPath("$.description").isEmpty())
                .andExpect(jsonPath("$.cloneUrl").isEmpty())
                .andExpect(jsonPath("$.stars").isEmpty())
                .andExpect(jsonPath("$.createdAt").isEmpty());
        verify(1, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
    }
}
