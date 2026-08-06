package com.acorn.gymmanagement;

import com.acorn.gymmanagement.dashboard.dto.response.DashboardResponse;
import com.acorn.gymmanagement.dashboard.dto.response.DashboardSummaryResponse;
import com.acorn.gymmanagement.dashboard.service.DashboardService;
import com.acorn.gymmanagement.security.SessionUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @BeforeEach
    void setUpDashboard() {
        DashboardSummaryResponse summary = new DashboardSummaryResponse(
                0, 0, 0, 0, BigDecimal.ZERO, 0, 0, 0, 0
        );
        when(dashboardService.getDashboard()).thenReturn(new DashboardResponse(
                "테스트 날짜", summary, 0, List.of(), List.of(), List.of()
        ));
    }

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("name=\"_csrf\"")
                ));
    }

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(
                        "Location",
                        "/login?redirect=%2Fadmin%2Fdashboard"
                ));
    }

    @Test
    void adminCanAccessAdminPage() throws Exception {
        mockMvc.perform(get("/admin/dashboard").session(session(SessionUser.ROLE_ADMIN)))
                .andExpect(status().isOk());
    }

    @Test
    void memberCannotAccessAdminPage() throws Exception {
        mockMvc.perform(get("/admin/dashboard").session(session(SessionUser.ROLE_MEMBER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void trainerCanAccessTrainerPage() throws Exception {
        mockMvc.perform(get("/trainer/home").session(session(SessionUser.ROLE_TRAINER)))
                .andExpect(status().isOk());
    }

    @Test
    void memberCanAccessMemberPage() throws Exception {
        mockMvc.perform(get("/member/home").session(session(SessionUser.ROLE_MEMBER)))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousApiRequestReturnsUnauthorizedJson() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("""
                        {
                          "success": false,
                          "error": {
                            "code": "UNAUTHORIZED"
                          }
                        }
                        """));
    }

    @Test
    void memberApiRequestReturnsForbiddenJson() throws Exception {
        mockMvc.perform(get("/api/members").session(session(SessionUser.ROLE_MEMBER)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("""
                        {
                          "success": false,
                          "error": {
                            "code": "FORBIDDEN"
                          }
                        }
                        """));
    }

    @Test
    void invalidRoleSessionIsTreatedAsAnonymous() throws Exception {
        mockMvc.perform(get("/admin/dashboard").session(session("UNKNOWN")))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void authenticatedUserCanLogout() throws Exception {
        MockHttpSession session = session(SessionUser.ROLE_MEMBER);

        mockMvc.perform(post("/logout").with(csrf()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThrows(
                IllegalStateException.class,
                () -> session.getAttribute(SessionUser.SESSION_KEY)
        );
    }

    private MockHttpSession session(String role) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                SessionUser.SESSION_KEY,
                new SessionUser(1L, "tester", "tester@fitflow.com", role)
        );
        return session;
    }
}
