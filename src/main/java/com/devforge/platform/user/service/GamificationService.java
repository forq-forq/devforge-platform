package com.devforge.platform.user.service;

import com.devforge.platform.course.domain.LessonType;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {

    private final UserRepository userRepository;

    @Transactional
    public void awardXp(User user, LessonType lessonType) {
        int points = switch (lessonType) {
            case LECTURE -> 10;
            case QUIZ -> 20;
            case PRACTICE -> 50;
        };

        user.setXp(user.getXp() + points);
        userRepository.save(user);
        
        log.info("Awarded {} XP to user {}. New Total: {}", points, user.getEmail(), user.getXp());
    }
}