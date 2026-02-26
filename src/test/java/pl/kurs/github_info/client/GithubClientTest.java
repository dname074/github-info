package pl.kurs.github_info.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import pl.kurs.github_info.model.RepoInfo;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@SpringBootTest()
@EnableWireMock({
        @ConfigureWireMock(name = "githubClient", port = 8081)
})
public class GithubClientTest {
    @InjectWireMock("githubClient")
    WireMockServer githubClientMock;
    @Autowired
    private GithubClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRepository_ResponseStatus200_RepoInfoReturned() throws JsonProcessingException {
        RepoInfo repoInfo = RepoInfo.builder()
                .fullName("owner/repoName")
                .cloneUrl("cloneUrl")
                .description("description")
                .stars(1)
                .createdAt(LocalDateTime.of(2001, 10, 15, 20, 0, 0))
                .build();
        githubClientMock.stubFor(get("/repos/owner/repoName").willReturn(
                aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(repoInfo))
                        .withStatus(200)
        ));
        RepoInfo result = client.getRepository("owner", "repoName");

        Assertions.assertAll(
                () -> assertEquals("owner/repoName", result.getFullName()),
                () -> assertEquals("cloneUrl", result.getCloneUrl()),
                () -> assertEquals("description", result.getDescription()),
                () -> assertEquals(1, result.getStars()),
                () -> assertEquals(LocalDateTime.of(2001, 10, 15, 20, 0, 0), result.getCreatedAt())
        );
        verify(getRequestedFor(urlEqualTo("/repos/owner/repoName")));
    }

    @Test
    void getRepository_ResponseStatus503_RetryableExceptionThrown() {
        githubClientMock.stubFor(get(urlEqualTo("/repos/owner/repoName"))
                .willReturn(aResponse()
                        .withStatus(503)));
        RepoInfo repoInfo = client.getRepository("owner", "repoName");
        Assertions.assertAll(
                () -> assertNull(repoInfo.getFullName()),
                () -> assertNull(repoInfo.getCreatedAt()),
                () -> assertNull(repoInfo.getCloneUrl()),
                () -> assertNull(repoInfo.getDescription()),
                () -> assertNull(repoInfo.getStars())
        );
        verify(3, getRequestedFor(urlEqualTo("/repos/owner/repoName")));
    }
}
