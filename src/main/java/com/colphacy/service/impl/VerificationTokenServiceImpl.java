package com.colphacy.service.impl;

import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.VerificationToken;
import com.colphacy.repository.VerificationTokenRepository;
import com.colphacy.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Override
    public VerificationToken findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
