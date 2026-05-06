package com.nomba.dsaapp.exception;

import java.util.UUID;

public class DsaNotFoundException extends RuntimeException {
    public DsaNotFoundException(UUID dsaId) {
        super("DSA not found with id: " + dsaId);
    }
}