package pl.kurs.github_info.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kurs.github_info.model.RepoInfo;

import java.util.Optional;

@Repository
public interface GithubInfoRepository extends JpaRepository<RepoInfo, Long> {
    @Query("""
            select r
            from RepoInfo r
            where r.fullName = concat(:owner, '/', :repositoryName)
            """)
    Optional<RepoInfo> findByOwnerAndRepositoryName(
            @Param("owner") String owner,
            @Param("repositoryName") String repositoryName
    );
}
