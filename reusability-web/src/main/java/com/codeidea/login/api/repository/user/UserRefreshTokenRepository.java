package com.codeidea.login.api.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeidea.login.api.entity.user.UserRefreshToken;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

	UserRefreshToken findByUserId(String userId);

	UserRefreshToken findByUserIdAndRefreshToken(String userId, String refreshToken);
}
