package com.acorn.gymmanagement.auth.mapper;

import com.acorn.gymmanagement.auth.model.UserAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AuthMapper {

    Optional<UserAccount> findByLoginId(String loginId);
}
