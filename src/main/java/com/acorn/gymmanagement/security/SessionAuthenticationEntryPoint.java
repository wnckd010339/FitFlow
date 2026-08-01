package com.acorn.gymmanagement.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SessionAuthenticationEntryPoint  implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        if(isApiRequest(request)){
            writeUnauthorizedResponse(response);
            return;
        }

        String requestPath = buildRequestPath(request);
        String encodedPath = UriUtils.encode(
                requestPath,
                StandardCharsets.UTF_8
        );

        response.sendRedirect(
                request.getContextPath()
                    +"/login?redirect="
                    +encodedPath
        );
    }

    private boolean isApiRequest(HttpServletRequest request){
        return request.getRequestURI().startsWith(
                request.getContextPath() + "/api/"
        );
    }

    private String buildRequestPath(HttpServletRequest request){
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        String path = requestUri.substring(contextPath.length());
        String queryString = request.getQueryString();

        if(queryString == null || queryString.isBlank()){
            return path;
        }

        return path + "?" + queryString;
    }

    private void writeUnauthorizedResponse(
            HttpServletResponse response
    ) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write("""
                {
                    "success" : false,
                    "message" : "로그인이 필요합니다.",
                    "error" : {
                        "code" : "UNAUTHORIZED",
                        "detail" : "인증된 사용자만 접근할 수 있습니다."
                    }
                }
                """);
    }
}
