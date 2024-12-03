package ru.kkettch.service;

import ru.kkettch.dto.JWTRequestDto;
import ru.kkettch.dto.JwtResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    JwtResponseDto login(JWTRequestDto jwtRequestDto, HttpServletResponse response);

    JwtResponseDto register(JWTRequestDto jwtRequestDto, HttpServletResponse response);

    JwtResponseDto logout(HttpServletResponse response);
}
