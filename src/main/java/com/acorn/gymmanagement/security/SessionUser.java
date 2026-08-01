package com.acorn.gymmanagement.security;

import java.io.Serializable;
import java.util.Set;

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

    private static final Set<String> SUPPORTED_ROLES = Set.of(
            ROLE_ADMIN,
            ROLE_MEMBER,
            ROLE_TRAINER
    );

    public boolean admin(){
        return ROLE_ADMIN.equals(role);
    }

    public boolean trainer(){
        return ROLE_TRAINER.equals(role);
    }

    public boolean member(){
        return ROLE_MEMBER.equals(role);
    }

    public boolean hasValidRole(){
        return SUPPORTED_ROLES.contains(role);
    }

    public String authority(){
        if(!hasValidRole()){
            throw new IllegalStateException(
                    "지원하지 않는 사용자 역할입니다 : " + role
            );
        }

        return "ROLE_" + role;
    }

    public boolean canAccess(String path){
        if(path == null || path.isBlank()){
            return false;
        }

        return switch(role){
            case ROLE_ADMIN -> matchesArea(path, "/admin");
            case ROLE_TRAINER -> matchesArea(path, "/trainer");
            case ROLE_MEMBER -> matchesArea(path, "/member");
            default -> false;
        };
    }

    private boolean matchesArea(String path, String area) {
        return path.equals(area) || path.startsWith(area + "/");
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
