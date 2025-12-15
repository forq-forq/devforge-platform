package com.devforge.platform.user.web.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String bio;
    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;
}