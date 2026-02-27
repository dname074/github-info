package pl.kurs.github_info.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.github_info.dto.ErrorMessageDto;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.service.GithubInfoService;

@RequestMapping("/repositories")
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "Github operations")
public class GithubInfoController {
    private final GithubInfoService service;

    @Operation(summary = "Get informations about github repository by owner name and repo name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repository exists, data returned",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RepoInfoDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Repository not found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessageDto.class))
                    })
    }
    )
    @GetMapping("/{owner}/{repositoryName}")
    public RepoInfoDto getRepository(@PathVariable @NotBlank String owner, @PathVariable @NotBlank String repositoryName) {
        log.info("Received GET /repositories/{}/{} request", owner, repositoryName);
        return service.getRepository(owner, repositoryName);
    }
}
