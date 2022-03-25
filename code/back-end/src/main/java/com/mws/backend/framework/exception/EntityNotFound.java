package com.mws.backend.framework.exception;

public class EntityNotFound extends RuntimeException {
    public EntityNotFound(final String message) {
        super(message);
    }
}
