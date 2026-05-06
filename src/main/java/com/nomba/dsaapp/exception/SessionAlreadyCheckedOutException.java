package com.nomba.dsaapp.exception;

import java.util.UUID;

public class SessionAlreadyCheckedOutException extends RuntimeException {
    public SessionAlreadyCheckedOutException(UUID sessionId) {
        super("Session already checked out: " + sessionId);
    }
}