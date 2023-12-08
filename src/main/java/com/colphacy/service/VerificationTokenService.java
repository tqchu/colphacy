package com.colphacy.service;

import com.colphacy.model.VerificationToken;

public interface VerificationTokenService {
    VerificationToken findByToken(String token);
}
