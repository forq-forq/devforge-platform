package com.devforge.platform.practice.web.dto;

/**
 * Result returned to the frontend.
 */
public record RunCodeResponse(
    boolean success,
    String message,
    String logs
) {}