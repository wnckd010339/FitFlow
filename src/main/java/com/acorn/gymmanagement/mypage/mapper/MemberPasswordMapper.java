package com.acorn.gymmanagement.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberPasswordMapper {
    Optional<String> findPasswordHash(@Param("userId") Long userId);
    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);
}
