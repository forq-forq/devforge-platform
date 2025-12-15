package com.devforge.platform.user.web;

import com.devforge.platform.user.domain.Role;
import com.devforge.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LeaderboardController {

    private final UserRepository userRepository;

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        var topStudents = userRepository.findTop10ByRoleOrderByXpDesc(Role.STUDENT);
        model.addAttribute("leaders", topStudents);
        return "user/leaderboard";
    }
}