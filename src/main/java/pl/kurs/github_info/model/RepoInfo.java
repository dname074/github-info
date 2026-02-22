package pl.kurs.github_info.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RepoInfo {
    private String fullName;
    private String description;
    private String cloneUrl;
    private int starts;
    private LocalDateTime createdAt;
}
