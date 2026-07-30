package com.acorn.gymmanagement.auth.service;

import com.acorn.gymmanagement.security.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    public void saveUser(HttpSession session, SessionUser sessionUser){
        session.setAttribute(SessionUser.SESSION_KEY, sessionUser);
    }

    public Optional<SessionUser> getUser(HttpSession session){
        Object value = session.getAttribute(SessionUser.SESSION_KEY);

        if(value instanceof SessionUser sessionUser){
            return Optional.of(sessionUser);
        }

        return Optional.empty();
    }

    public void logout(HttpSession session){
        session.invalidate();
    }
}
