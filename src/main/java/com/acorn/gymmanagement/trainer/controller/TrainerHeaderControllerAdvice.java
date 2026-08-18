package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.dto.response.TrainerProfileView;
import com.acorn.gymmanagement.trainer.service.TrainerMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

@ControllerAdvice(assignableTypes = {
        TrainerHomeController.class,
        TrainerMemberController.class,
        TrainerRoutineController.class,
        TrainerWorkoutController.class,
        TrainerProfileController.class
})
@RequiredArgsConstructor
public class TrainerHeaderControllerAdvice {
    private final TrainerMemberService trainerMemberService;

    @ModelAttribute("headerProfile")
    public TrainerProfileView headerProfile(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser) {
        return trainerMemberService.profile(sessionUser.userId());
    }
}
