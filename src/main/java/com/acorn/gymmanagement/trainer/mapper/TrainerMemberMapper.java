package com.acorn.gymmanagement.trainer.mapper;

import com.acorn.gymmanagement.trainer.dto.response.TrainerAttendanceView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerHomeMemberResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerHomeProfileResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerMemberDetailView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerMemberView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerProfileView;
import com.acorn.gymmanagement.trainer.form.TrainerProfileForm;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrainerMemberMapper {
    Optional<Long> findTrainerId(@Param("userId") Long userId);
    boolean existsAssignedMember(@Param("trainerId") Long trainerId, @Param("memberId") Long memberId);
    TrainerHomeProfileResponse findHomeProfile(@Param("userId") Long userId);
    List<TrainerHomeMemberResponse> findHomeMembers(@Param("userId") Long userId);
    TrainerProfileView findProfile(@Param("userId") Long userId);
    int updateProfile(@Param("userId") Long userId, @Param("form") TrainerProfileForm form);
    List<TrainerMemberView> findMembers(@Param("userId") Long userId, @Param("keyword") String keyword);
    Optional<TrainerMemberDetailView> findMemberDetail(
            @Param("userId") Long userId, @Param("memberId") Long memberId);
    List<TrainerAttendanceView> findAttendances(@Param("memberId") Long memberId);
}
