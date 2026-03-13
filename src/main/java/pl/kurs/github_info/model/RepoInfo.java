package pl.kurs.github_info.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "Repo_info")
public class RepoInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fullName", unique = true)
    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    private LocalDateTime createdAt;

    public void update(RepoInfo repoInfo) {
        this.fullName = repoInfo.getFullName();
        this.description = repoInfo.getDescription();
        this.cloneUrl = repoInfo.getCloneUrl();
        this.stars = repoInfo.getStars();
        this.createdAt = repoInfo.getCreatedAt();
    }

    public boolean areFieldsNull() {
        return fullName == null &&
                description == null &&
                cloneUrl == null &&
                stars == null &&
                createdAt == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepoInfo repoInfo = (RepoInfo) o;
        return id != null && Objects.equals(id, repoInfo.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
