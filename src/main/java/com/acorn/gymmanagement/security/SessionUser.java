package com.acorn.gymmanagement.security;

import java.io.Serializable;

public record SessionUser(
        Long userId,
        String loginId,
        String email,
        String role
)  implements Serializable {

    public static final String SESSION_KEY = "LOGIN_USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TRAINER = "TRAINER";
    public static final String ROLE_MEMBER = "MEMBER";

    public boolean admin(){
        return ROLE_ADMIN.equals(role);
    }

    public boolean trainer(){
        return ROLE_TRAINER.equals(role);
    }

    public boolean member(){
        return ROLE_MEMBER.equals(role);
    }

    public String defaultRedirectPath(){
        return switch(role){
            case ROLE_ADMIN -> "/admin/dashboard";
            case ROLE_TRAINER -> "/trainer/home";
            case ROLE_MEMBER -> "/member/home";
            default -> "/login";
        };
    }
}
