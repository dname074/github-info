package pl.kurs.github_info.dto;

import org.springframework.http.HttpStatus;

public record GithubInfoExceptionDto(String message, HttpStatus status) {
}
