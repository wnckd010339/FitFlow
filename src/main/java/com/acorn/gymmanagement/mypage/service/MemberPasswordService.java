package com.acorn.gymmanagement.mypage.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.mypage.form.PasswordChangeForm;
import com.acorn.gymmanagement.mypage.mapper.MemberPasswordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberPasswordService {
    private final MemberPasswordMapper memberPasswordMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void change(Long userId, PasswordChangeForm form) {
        String currentHash = memberPasswordMapper.findPasswordHash(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "변경할 회원 계정을 찾을 수 없습니다."));
        if (!passwordEncoder.matches(form.currentPassword(), currentHash)) {
            throw new BusinessException(ErrorCode.CONFLICT, "현재 비밀번호가 일치하지 않습니다.");
        }
        if (!form.newPassword().equals(form.newPasswordConfirmation())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(form.newPassword(), currentHash)) {
            throw new BusinessException(ErrorCode.CONFLICT, "현재 비밀번호와 다른 비밀번호를 사용해 주세요.");
        }
        if (memberPasswordMapper.updatePassword(userId, passwordEncoder.encode(form.newPassword())) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "비밀번호를 변경하지 못했습니다.");
        }
    }
}
