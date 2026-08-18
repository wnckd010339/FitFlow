package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.trainer.dto.response.*;
import com.acorn.gymmanagement.trainer.form.TrainerProfileForm;
import com.acorn.gymmanagement.trainer.mapper.TrainerMemberMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerMemberService {
    private final TrainerMemberMapper trainerMemberMapper;
    private final TrainerAccessValidator accessValidator;

    public TrainerHomeProfileResponse homeProfile(Long userId) {
        TrainerHomeProfileResponse profile = trainerMemberMapper.findHomeProfile(userId);
        if (profile == null) throw trainerNotFound();
        return profile;
    }

    public List<TrainerHomeMemberResponse> homeMembers(Long userId) {
        return trainerMemberMapper.findHomeMembers(userId);
    }

    public TrainerProfileView profile(Long userId) {
        TrainerProfileView profile = trainerMemberMapper.findProfile(userId);
        if (profile == null) throw trainerNotFound();
        return profile;
    }

    public List<TrainerMemberView> members(Long userId, String keyword) {
        return trainerMemberMapper.findMembers(userId, keyword);
    }

    public TrainerMemberDetailView member(Long userId, Long memberId) {
        accessValidator.requireAssignedMember(userId, memberId);
        return trainerMemberMapper.findMemberDetail(userId, memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
    }

    public List<TrainerAttendanceView> attendances(Long userId, Long memberId) {
        accessValidator.requireAssignedMember(userId, memberId);
        return trainerMemberMapper.findAttendances(memberId);
    }

    @Transactional
    public void updateProfile(Long userId, TrainerProfileForm form) {
        var normalized = new TrainerProfileForm(
                form.name().trim(), normalizePhone(form.phone()), blankToNull(form.specialty()));
        if (trainerMemberMapper.updateProfile(userId, normalized) != 1) {
            throw trainerNotFound();
        }
    }

    private BusinessException trainerNotFound() {
        return new BusinessException(ErrorCode.NOT_FOUND, "트레이너 정보를 찾을 수 없습니다.");
    }

    private String normalizePhone(String value) {
        String digits = value.replaceAll("[^0-9]", "");
        return digits.length() == 11
                ? digits.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3")
                : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
