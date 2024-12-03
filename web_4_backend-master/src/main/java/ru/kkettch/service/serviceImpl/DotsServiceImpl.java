package ru.kkettch.service.serviceImpl;

import ru.kkettch.dto.DotsRequestDto;
import ru.kkettch.dto.DotsResponseDto;
import ru.kkettch.entity.DotsEntity;
import ru.kkettch.entity.HitType;
import ru.kkettch.exceptions.AppException;
import ru.kkettch.repository.DotsRepository;
import ru.kkettch.service.DotsService;
import ru.kkettch.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class DotsServiceImpl implements DotsService {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    private static final short COUNT_OF_NUMBERS_AFTER_DECIMAL_POINT = 3;
    private static final double DIVIDER = Math.pow(10, COUNT_OF_NUMBERS_AFTER_DECIMAL_POINT);
    private final DotsRepository dotsRepository;
    private final UserService userService;

    public DotsServiceImpl(DotsRepository dotsRepository, UserService userService) {
        this.dotsRepository = dotsRepository;
        this.userService = userService;
    }


    @Override
    public List<DotsResponseDto> getAllUserDots(Authentication authentication) {
        List<DotsEntity> dotsEntityList = dotsRepository.getDotsEntitiesByUserId(userService.findUserByUserName(authentication.getName()).get().getId());
        return dotsEntityList.stream().map(this::mapDotsEntityToResponseDotsDto).toList();
    }

    @Override
    public DotsEntity addDot(DotsRequestDto dotsRequestDto, Authentication authentication) {
        long startExec = System.nanoTime();
        DotsEntity dotsEntity = new DotsEntity();
        dotsEntity.setCurRequestTime(dateFormat.format(new Date(System.currentTimeMillis())));
        dotsEntity.setHitType(checkArea(dotsRequestDto));
        dotsEntity.setX(dotsRequestDto.x());
        dotsEntity.setY(dotsRequestDto.y());
        dotsEntity.setR(dotsRequestDto.r());
        dotsEntity.setExecutionTime(System.nanoTime() - startExec);
        dotsEntity.setUser(userService.findUserByUserName(authentication.getName()).get());
        return dotsRepository.save(dotsEntity);
    }

    @Override
    @Transactional
    public int deleteDotsByUserId(Authentication authentication) {
        int dotsDeletedCount = dotsRepository.deleteAllByUserId(userService.findUserByUserName(authentication.getName()).get().getId());
        if (dotsDeletedCount >= 0) {
            return dotsDeletedCount;
        } else {
            throw new AppException("Dot were not deleted", HttpStatus.BAD_REQUEST);
        }
    }

    private String checkArea(DotsRequestDto dotsRequestDto) {
        if (dotsRequestDto.y() <= dotsRequestDto.x() / 2 + dotsRequestDto.r() / 2 && dotsRequestDto.y() >= 0 && dotsRequestDto.x() <= 0)
            return HitType.HIT.getHitArea();
        if (dotsRequestDto.x() * dotsRequestDto.x() + dotsRequestDto.y() * dotsRequestDto.y() <= dotsRequestDto.r() * dotsRequestDto.r() && dotsRequestDto.x() <= 0 && dotsRequestDto.y() <= 0)
            return HitType.HIT.getHitArea();
        if (dotsRequestDto.x() >= 0 && dotsRequestDto.x() <= dotsRequestDto.r() && dotsRequestDto.y() >= 0 && dotsRequestDto.y() <= dotsRequestDto.r())
            return HitType.HIT.getHitArea();
        return HitType.MISS.getHitArea();
    }

    private boolean validateDots(DotsRequestDto dotsRequestDto) {
        if (dotsRequestDto.x() < -4 || dotsRequestDto.x() > 4)
            return false;
        if (dotsRequestDto.y() < -5 || dotsRequestDto.y() > 3)
            return false;
        return !(dotsRequestDto.r() < -4) && !(dotsRequestDto.r() > 4);
    }

    public DotsResponseDto mapDotsEntityToResponseDotsDto(DotsEntity dotsEntity) {
        double x = Math.round(dotsEntity.getX() * DIVIDER) / DIVIDER;
        double y = Math.round(dotsEntity.getY() * DIVIDER) / DIVIDER;
        double r = Math.round(dotsEntity.getR() * DIVIDER) / DIVIDER;
        return new DotsResponseDto(x, y, r, dotsEntity.getCurRequestTime(),
                dotsEntity.getExecutionTime(),
                dotsEntity.getHitType()
        );
    }
}
