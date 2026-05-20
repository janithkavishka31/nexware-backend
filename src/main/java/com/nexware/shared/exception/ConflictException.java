package com.nexware.shared.exception;

import com.nexware.shared.api.ErrorCode;

public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
