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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.exception.RepositoryNotFoundException;
import pl.kurs.github_info.service.GithubInfoService;

@RequestMapping("/repositories")
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "Github operations")
public class GithubInfoController {
    private final GithubInfoService service;

    @Operation(summary = "Get information about github repository by owner name and repo name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repository exists, data returned",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RepoInfoDto.class))
                    })
    })
    @GetMapping("/{owner}/{repositoryName}")
    public RepoInfoDto getRepository(@PathVariable @NotBlank String owner,
                                     @PathVariable @NotBlank String repositoryName) {
        log.info("Received GET /repositories/{}/{} request", owner, repositoryName);
        return service.getRepository(owner, repositoryName);
    }

    @Operation(summary = "Get information about github repository from local device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repository found, data returned",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RepoInfoDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Repository not found",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RepositoryNotFoundException.class))
            })
    })
    @GetMapping("/local/{owner}/{repositoryName}")
    public RepoInfoDto getRepositoryFromLocal(@PathVariable @NotBlank String owner,
                                              @PathVariable @NotBlank String repositoryName) {
        log.info("Received GET /repositories/local/{}/{} request", owner, repositoryName);
        return service.getRepositoryFromLocal(owner, repositoryName);
    }

    @Operation(summary = "Save information about github repository to local device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Repository found and saved to local device",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RepoInfoDto.class))
            })
    })
    @PostMapping("/{owner}/{repositoryName}")
    @ResponseStatus(HttpStatus.CREATED)
    public RepoInfoDto saveRepositoryToLocal(@PathVariable @NotBlank String owner,
                                             @PathVariable @NotBlank String repositoryName) {
        log.info("Received POST /repositories/{}/{} request", owner, repositoryName);
        return service.saveRepositoryToLocal(owner, repositoryName);
    }

    @Operation(summary = "Update information about locally saved repository")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repository found and updated",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RepoInfoDto.class))
            })
    })
    @PutMapping("/{owner}/{repositoryName}")
    public RepoInfoDto updateRepositoryFromLocal(@PathVariable @NotBlank String owner,
                                               @PathVariable @NotBlank String repositoryName) {
        log.info("Received PUT /repositories/{}/{} request", owner, repositoryName);
        return service.updateRepositoryFromLocal(owner, repositoryName);
    }

    @Operation(summary = "Delete repository information from local device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repository found and deleted",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RepoInfoDto.class))
            })
    })
    @DeleteMapping("/{owner}/{repositoryName}")
    public RepoInfoDto deleteRepositoryFromLocal(@PathVariable @NotBlank String owner,
                                                 @PathVariable @NotBlank String repositoryName) {
        log.info("Received DELETE /repositories/{}/{} request", owner, repositoryName);
        return service.deleteRepositoryFromLocal(owner, repositoryName);
    }
}
