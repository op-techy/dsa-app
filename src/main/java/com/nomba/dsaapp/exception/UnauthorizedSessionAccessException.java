package com.nomba.dsaapp.exception;
import java.util.UUID;

public class UnauthorizedSessionAccessException extends RuntimeException {
    public UnauthorizedSessionAccessException(UUID sessionId) {
        super("You do not have permission to check out session: " + sessionId);
    }
}