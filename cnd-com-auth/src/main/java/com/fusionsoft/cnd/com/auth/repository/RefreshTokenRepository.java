package com.fusionsoft.cnd.com.auth.repository;

import com.fusionsoft.cnd.com.auth.domain.entity.User;
import com.fusionsoft.cnd.com.auth.security.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);

}