package com.mws.back_end.framework.exception;

public class EntityNotFound extends RuntimeException {
    public EntityNotFound(final String message) {
        super(message);
    }
}

