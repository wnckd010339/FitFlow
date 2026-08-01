package com.acorn.gymmanagement.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class SessionAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException, ServletException{

        if(isApiRequest(request)){
            writeForbiddenResponse(response);
            return;
        }

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "접근 권한이 없습니다."
        );
    }

    private boolean isApiRequest(HttpServletRequest request){
        return request.getRequestURI().startsWith(
                request.getContextPath() + "/api/"
        );
    }

    private void writeForbiddenResponse(
            HttpServletResponse response
    ) throws IOException{
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write("""
                {
                    "success" : false,
                    "message" : "접근 권한이 없습니다.",
                    "error" : {
                        "code" : "FORBIDDEN",
                        "detail" : "현재 역할로 접근할 수 없는 기능입니다."
                        }
                }
                """);
    }
}
