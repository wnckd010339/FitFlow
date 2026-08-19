package com.acorn.gymmanagement.mypage.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.mypage.form.PasswordChangeForm;
import com.acorn.gymmanagement.mypage.mapper.MemberPasswordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberPasswordServiceTest {
    @Mock MemberPasswordMapper mapper;
    @Mock PasswordEncoder encoder;
    MemberPasswordService service;

    @BeforeEach void setUp() { service = new MemberPasswordService(mapper, encoder); }

    @Test void changesPasswordAfterCurrentPasswordVerification() {
        when(mapper.findPasswordHash(1L)).thenReturn(Optional.of("old-hash"));
        when(encoder.matches("current123", "old-hash")).thenReturn(true);
        when(encoder.matches("newPassword123", "old-hash")).thenReturn(false);
        when(encoder.encode("newPassword123")).thenReturn("new-hash");
        when(mapper.updatePassword(1L, "new-hash")).thenReturn(1);

        service.change(1L, new PasswordChangeForm("current123", "newPassword123", "newPassword123"));

        verify(mapper).updatePassword(1L, "new-hash");
    }

    @Test void rejectsWrongCurrentPassword() {
        when(mapper.findPasswordHash(1L)).thenReturn(Optional.of("old-hash"));
        when(encoder.matches("wrong", "old-hash")).thenReturn(false);
        assertThrows(BusinessException.class, () -> service.change(
                1L, new PasswordChangeForm("wrong", "newPassword123", "newPassword123")));
        verify(mapper, never()).updatePassword(any(), any());
    }
}
