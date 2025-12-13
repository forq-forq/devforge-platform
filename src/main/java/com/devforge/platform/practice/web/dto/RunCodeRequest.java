package com.devforge.platform.practice.web.dto;

/**
 * Payload sent by the frontend when user clicks "Run".
 */
public record RunCodeRequest(
    String code
) {}