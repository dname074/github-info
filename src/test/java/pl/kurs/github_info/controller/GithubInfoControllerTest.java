package pl.kurs.github_info.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.exception.RepositoryNotFoundException;
import pl.kurs.github_info.service.GithubInfoService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class GithubInfoControllerTest {
    @MockitoBean
    GithubInfoService service;
    @Autowired
    MockMvc mockMvc;

    @Test
    void getRepository_DataCorrect_RepoInfoReturnedInJson() throws Exception {
        String owner = "owner";
        String repositoryName = "repo";
        RepoInfoDto repoInfoDto = new RepoInfoDto("fullName", null, "url", 1, LocalDateTime.of(2015, 8, 15, 20, 0, 0));
        when(service.getRepository(anyString(), anyString())).thenReturn(repoInfoDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/{owner}/{repositoryName}", owner, repositoryName))
                .andDo(print())
                .andExpect(jsonPath("$.fullName").value("fullName"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.cloneUrl").value("url"))
                .andExpect(jsonPath("$.stars").value(1))
                .andExpect(jsonPath("$.createdAt").value("2015-08-15T20:00:00"));
        verify(service,times(1)).getRepository("owner", "repo");
        verifyNoMoreInteractions(service);
    }

    @Test
    void getRepository_RepositoryNotFoundExceptionThrown_404Returned() throws Exception {
        String owner = "owner";
        String repositoryName = "repo";
        when(service.getRepository(anyString(),anyString())).thenThrow(new RepositoryNotFoundException("Nie znaleziono podanego repozytorium"));

        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/{owner}/{repositoryName}", owner, repositoryName))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono podanego repozytorium"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
        verify(service,times(1)).getRepository("owner", "repo");
        verifyNoMoreInteractions(service);
    }
}
