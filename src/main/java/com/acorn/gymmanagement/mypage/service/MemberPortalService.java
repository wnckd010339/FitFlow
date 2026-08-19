package com.acorn.gymmanagement.mypage.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.mypage.dto.response.*;
import com.acorn.gymmanagement.mypage.form.MemberProfileForm;
import com.acorn.gymmanagement.mypage.mapper.MemberPortalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPortalService {
    private final MemberPortalMapper memberPortalMapper;

    public List<MemberMembershipView> memberships(Long userId) { return memberPortalMapper.findMemberships(userId); }
    public List<MemberAttendanceView> attendances(Long userId) { return memberPortalMapper.findAttendances(userId); }
    public List<MemberPaymentView> payments(Long userId) { return memberPortalMapper.findPayments(userId); }

    public MemberProfileView profile(Long userId) {
        return memberPortalMapper.findProfile(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateProfile(Long userId, MemberProfileForm form) {
        MemberProfileForm normalized = new MemberProfileForm(
                form.name().trim(), normalizePhone(form.phone()), form.birthDate(), form.gender(),
                form.email() == null || form.email().isBlank() ? null : form.email().trim().toLowerCase()
        );
        if (memberPortalMapper.updateMemberProfile(userId, normalized) != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "수정할 회원 정보를 찾을 수 없습니다.");
        }
        try {
            if (memberPortalMapper.updateUserEmail(userId, normalized.email()) != 1) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "수정할 계정 정보를 찾을 수 없습니다.");
            }
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
    }

    private String normalizePhone(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11) return digits.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        if (digits.length() == 10) return digits.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        return phone.trim();
    }
}
