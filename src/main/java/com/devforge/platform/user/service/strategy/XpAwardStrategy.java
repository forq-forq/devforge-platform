package com.devforge.platform.user.service.strategy;

import com.devforge.platform.course.domain.LessonType;

public interface XpAwardStrategy {
    LessonType getSupportedType();
    int getXpAmount();
}