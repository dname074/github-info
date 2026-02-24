package pl.kurs.github_info.dto;

import java.time.LocalDateTime;

public record RepoInfoDto(
        String fullName,
        String description,
        String cloneUrl,
        Integer stars,
        LocalDateTime createdAt
) {
}
