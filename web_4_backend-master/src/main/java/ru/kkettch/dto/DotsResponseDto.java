package ru.kkettch.dto;

public record DotsResponseDto(
        double x,
        double y,
        double r,
        String curRequestTime,
        long executionTime,
        String hitType
) {
}
