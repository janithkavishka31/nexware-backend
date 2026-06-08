package com.nexware.auth.exception;

import com.nexware.shared.api.ErrorCode;
import com.nexware.shared.exception.DomainException;

public class AuthenticationFailedException extends DomainException {

    public AuthenticationFailedException(String message) {
        super(ErrorCode.AUTHENTICATION_FAILED, message);
    }
}
