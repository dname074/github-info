package pl.kurs.github_info.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kurs.github_info.model.RepoInfo;

@Repository
public interface GithubInfoRepository extends JpaRepository<RepoInfo, Long> {
}
