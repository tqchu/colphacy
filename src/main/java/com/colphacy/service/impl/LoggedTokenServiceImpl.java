package com.colphacy.service.impl;

import com.colphacy.model.LoggedToken;
import com.colphacy.repository.LoggedTokenRepository;
import com.colphacy.service.LoggedTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoggedTokenServiceImpl implements LoggedTokenService {
    private LoggedTokenRepository loggedTokenRepository;

    @Autowired
    public void setLoggedTokenRepository(LoggedTokenRepository loggedTokenRepository) {
        this.loggedTokenRepository = loggedTokenRepository;
    }

    @Override
    public Optional<LoggedToken> findByToken(String token) {
        return loggedTokenRepository.findLoggedTokenByToken(token);
    }

    @Override
    public LoggedToken save(LoggedToken loggedToken) {
        return loggedTokenRepository.save(loggedToken);
    }
}
