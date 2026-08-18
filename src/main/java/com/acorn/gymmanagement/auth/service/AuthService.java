package com.acorn.gymmanagement.auth.service;

import com.acorn.gymmanagement.auth.form.LoginForm;
import com.acorn.gymmanagement.auth.mapper.AuthMapper;
import com.acorn.gymmanagement.auth.model.LocalAuthenticatedUser;
import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.security.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String INVALID_CREDENTIALS_MESSAGE =
            "아이디 또는 비밀번호을 확인해 주세요.";

    private final AuthMapper authMapper;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public void login(HttpSession session, LoginForm form){
        LocalAuthenticatedUser user = authMapper
                .findLocalUserByLoginId(form.getLoginId())
                .orElseThrow(() -> invalidCredentials());

        if(!STATUS_ACTIVE.equals(user.status())){
            throw invalidCredentials();
        }

        if(!passwordEncoder.matches(
                form.getPassword(),
                user.passwordHash()
        )){
            throw invalidCredentials();
        }

        SessionUser sessionUser = new SessionUser(
                user.userId(),
                user.loginId(),
                user.email(),
                normalizeRole(user.role())
        );

        sessionService.saveUser(session, sessionUser);
    }

    private BusinessException invalidCredentials(){
        return new BusinessException(
                ErrorCode.AUTH_INVALID_CREDENTIALS,
                INVALID_CREDENTIALS_MESSAGE
        );
    }

    private String normalizeRole(String role) {
        return role != null && role.startsWith("ROLE_") ? role.substring(5) : role;
    }
}
