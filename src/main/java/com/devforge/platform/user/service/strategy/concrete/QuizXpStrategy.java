package com.devforge.platform.user.service.strategy.concrete;

import org.springframework.stereotype.Component;

import com.devforge.platform.course.domain.LessonType;
import com.devforge.platform.user.service.strategy.XpAwardStrategy;

@Component
public class QuizXpStrategy implements XpAwardStrategy {
    @Override
    public LessonType getSupportedType() {
        return LessonType.QUIZ;
    }

    @Override
    public int getXpAmount() {
        return 20;
    }
}