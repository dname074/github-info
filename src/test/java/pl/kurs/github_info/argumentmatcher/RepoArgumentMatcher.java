package pl.kurs.github_info.argumentmatcher;

import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;
import pl.kurs.github_info.model.RepoInfo;

import java.util.Objects;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class RepoArgumentMatcher implements ArgumentMatcher<RepoInfo> {
    private final RepoInfo repoInfo;

    @Override
    public boolean matches(RepoInfo repoInfo) {
        return nonNull(repoInfo) &&
                Objects.equals(repoInfo.getFullName(), this.repoInfo.getFullName()) &&
                Objects.equals(repoInfo.getDescription(), this.repoInfo.getDescription()) &&
                Objects.equals(repoInfo.getCloneUrl(), this.repoInfo.getCloneUrl()) &&
                Objects.equals(repoInfo.getStars(), this.repoInfo.getStars()) &&
                Objects.equals(repoInfo.getCreatedAt(), this.repoInfo.getCreatedAt());
    }
}
