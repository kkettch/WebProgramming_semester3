package ru.kkettch.service;

import ru.kkettch.dto.DotsRequestDto;
import ru.kkettch.dto.DotsResponseDto;
import ru.kkettch.entity.DotsEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface DotsService {
    DotsEntity addDot(DotsRequestDto dotsRequestDto, Authentication authentication);

    List<DotsResponseDto> getAllUserDots(Authentication authentication);

    DotsResponseDto mapDotsEntityToResponseDotsDto(DotsEntity dotsEntity);

    int deleteDotsByUserId(Authentication authentication);
}
