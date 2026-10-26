package com.naviqplc.cargoportal.repository;

import com.naviqplc.cargoportal.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginRepository extends JpaRepository<UserLogin, Integer> {

    Optional<UserLogin> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmailId(String emailId);

    boolean existsByUsernameAndUserIdNot(String username, Integer userId);

    boolean existsByEmailIdAndUserIdNot(String emailId, Integer userId);
}