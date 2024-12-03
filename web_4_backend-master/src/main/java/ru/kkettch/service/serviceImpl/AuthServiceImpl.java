package ru.kkettch.service.serviceImpl;

import com.auth0.jwt.JWT;
import ru.kkettch.dto.JWTRequestDto;
import ru.kkettch.dto.JwtResponseDto;
import ru.kkettch.exceptions.AppException;
import ru.kkettch.security.IJwtUtils;
import ru.kkettch.service.AuthService;
import ru.kkettch.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final IJwtUtils ijwtUtils;
    private final static short LOGOUT_COOKIE_AGE = 1;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserService userService, IJwtUtils ijwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.ijwtUtils = ijwtUtils;
    }

    @Override
    public JwtResponseDto login(JWTRequestDto jwtRequestDto, HttpServletResponse response) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequestDto.username(), jwtRequestDto.password()));
        } catch (AuthenticationException ex) {
            throw new AppException("Login or password are incorrect", HttpStatus.BAD_REQUEST);
        }

        String token = ijwtUtils.generateAccessToken(authentication);

        Cookie cookie = new Cookie("Token", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new JwtResponseDto(JWT.decode(token).getExpiresAt().getTime(), "JWT");
    }

    @Override
    public JwtResponseDto register(JWTRequestDto jwtRequestDto, HttpServletResponse response) {

        if (userService.isUserExistByName(jwtRequestDto.username())) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        userService.addUser(jwtRequestDto, "USER");
        JwtResponseDto jwtResponseDto = login(jwtRequestDto, response);
        return jwtResponseDto;
    }

    @Override
    public JwtResponseDto logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Token", "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(LOGOUT_COOKIE_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
        return new JwtResponseDto(new Date().getTime(), "JWT", "You are logged out");
    }
}
