package com.acorn.gymmanagement.auth.mapper;

import com.acorn.gymmanagement.auth.model.LocalAuthenticatedUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AuthMapper {

    Optional<LocalAuthenticatedUser> findLocalUserByLoginId(@Param("loginId") String loginId);
}
