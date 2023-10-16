package com.colphacy.repository;

import com.colphacy.model.LoggedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoggedTokenRepository extends JpaRepository<LoggedToken, Long> {
    Optional<LoggedToken> findLoggedTokenByToken(String token);
}
