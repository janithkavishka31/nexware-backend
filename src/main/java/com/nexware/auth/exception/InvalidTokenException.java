package com.nexware.auth.exception;

import com.nexware.shared.api.ErrorCode;
import com.nexware.shared.exception.DomainException;

public class InvalidTokenException extends DomainException {

    public InvalidTokenException(String message) {
        super(ErrorCode.AUTHENTICATION_FAILED, message);
    }
}
