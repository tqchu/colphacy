package com.colphacy.service;

import com.colphacy.model.LoggedToken;

import java.util.Optional;

public interface LoggedTokenService {
    Optional<LoggedToken> findByToken(String token);
    LoggedToken save(LoggedToken loggedToken);
}
