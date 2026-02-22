package pl.kurs.github_info.mapper;

import org.mapstruct.Mapper;
import pl.kurs.github_info.dto.RepoInfoDto;
import pl.kurs.github_info.model.RepoInfo;

@Mapper(componentModel = "spring")
public interface RepoInfoMapper {
    RepoInfoDto toDto(RepoInfo repoInfo);
}
