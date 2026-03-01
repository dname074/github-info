package pl.kurs.github_info.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import pl.kurs.github_info.dto.RepoInfoDto;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@SpringBootTest()
@AutoConfigureWireMock(port = 8081)
public class GithubClientTest {
    @Autowired
    WireMockServer githubClientMock;
    @Autowired
    private GithubClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        githubClientMock.resetRequests(); // bez tego w historii "serwera" wiremocka są requesty ze wszystkich testów i test retryowania sie wywala
    }

    @Test
    void getRepository_ResponseStatus200_RepoInfoReturned() throws JsonProcessingException {
        RepoInfoDto repoInfoDto = new RepoInfoDto("owner/repoName", "description", "cloneUrl", 1,
                LocalDateTime.of(2015, 10, 25, 20, 0, 0));
        githubClientMock.stubFor(get("/repos/owner/repoName").willReturn(
                aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(repoInfoDto))
                        .withStatus(200)
        ));
        RepoInfoDto result = client.getRepository("owner", "repoName");

        Assertions.assertAll(
                () -> assertEquals("owner/repoName", result.fullName()),
                () -> assertEquals("cloneUrl", result.cloneUrl()),
                () -> assertEquals("description", result.description()),
                () -> assertEquals(1, result.stars()),
                () -> assertEquals(LocalDateTime.of(2015, 10, 25, 20, 0, 0), result.createdAt())
        );
        verify(getRequestedFor(urlEqualTo("/repos/owner/repoName")));
    }

    @Test
    void getRepository_ResponseStatus503_3RetriesDoneAndRepoInfoReturned() {
        githubClientMock.stubFor(get("/repos/owner/repoName")
                .willReturn(aResponse()
                        .withStatus(503)));
        RepoInfoDto repoInfo = client.getRepository("owner", "repoName");
        Assertions.assertAll(
                () -> assertNull(repoInfo.fullName()),
                () -> assertNull(repoInfo.createdAt()),
                () -> assertNull(repoInfo.cloneUrl()),
                () -> assertNull(repoInfo.description()),
                () -> assertNull(repoInfo.stars())
        );
        verify(3, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
    }
}
