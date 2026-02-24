package pl.kurs.github_info.dto;

import org.springframework.http.HttpStatus;

public record ErrorMessageDto(String message, HttpStatus status) {
}
