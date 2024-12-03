package ru.kkettch.dto;

public record JWTRequestDto(
        String username,
        String password
) {
}
