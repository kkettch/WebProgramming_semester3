package ru.kkettch.service.serviceImpl;

import ru.kkettch.dto.JWTRequestDto;
import ru.kkettch.entity.UserEntity;
import ru.kkettch.repository.UserRepository;
import ru.kkettch.service.UserService;
import ru.kkettch.utils.PasswordUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    public UserServiceImpl(UserRepository userRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    @Override
    public Boolean isUserExistByName(String name) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(name);
        return optionalUser.isPresent();
    }

    @Override
    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> findUserByUserName(String name) {
        return userRepository.findByUsername(name);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = findUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s was not found", username)));

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        return new User(user.getUsername(), user.getPassword(), authorities);

    }

    @Override
    public UserEntity addUser(JWTRequestDto jwtRequestDto, String userRole) {
        UserEntity user = new UserEntity();
        user.setUsername(jwtRequestDto.username());
        user.setPassword(passwordUtils.hashPassword(jwtRequestDto.password()));
        user.setRole(userRole);
        return userRepository.save(user);
    }
}
