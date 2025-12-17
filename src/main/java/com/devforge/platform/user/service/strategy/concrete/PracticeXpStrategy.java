package com.devforge.platform.user.service.strategy.concrete;

import org.springframework.stereotype.Component;

import com.devforge.platform.course.domain.LessonType;
import com.devforge.platform.user.service.strategy.XpAwardStrategy;

@Component
public class PracticeXpStrategy implements XpAwardStrategy {
    @Override
    public LessonType getSupportedType() {
        return LessonType.PRACTICE;
    }

    @Override
    public int getXpAmount() {
        return 50;
    }
}