package com.devforge.platform.user.service;

import com.devforge.platform.course.domain.LessonType;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.repository.UserRepository;
import com.devforge.platform.user.service.strategy.XpAwardStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {

    private final UserRepository userRepository;
    private final Map<LessonType, XpAwardStrategy> xpStrategies;

    public GamificationService(UserRepository userRepository, List<XpAwardStrategy> strategies) {
        this.userRepository = userRepository;
        this.xpStrategies = strategies.stream()
                .collect(Collectors.toMap(XpAwardStrategy::getSupportedType, Function.identity()));
    }

    @Transactional
    public void awardXp(User user, LessonType lessonType) {
        XpAwardStrategy strategy = xpStrategies.get(lessonType);

        if (strategy == null) {
            log.warn("No XP strategy found for lesson type: {}", lessonType);
            return;
        }

        int points = strategy.getXpAmount();

        user.setXp(user.getXp() + points);
        userRepository.save(user);
        
        log.info("Awarded {} XP to user {}. New Total: {}", points, user.getEmail(), user.getXp());
    }
}