package ru.kkettch.service;

import ru.kkettch.dto.JWTRequestDto;
import ru.kkettch.entity.UserEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UserService extends UserDetailsService {
    UserEntity addUser(JWTRequestDto jwtRequestDto, String userRole);

    Boolean isUserExistByName(String name);

    List<UserEntity> findAllUsers();

    Optional<UserEntity> findUserByUserName(String name);

}
