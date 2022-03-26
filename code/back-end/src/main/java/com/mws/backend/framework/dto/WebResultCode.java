package com.mws.backend.framework.dto;

public enum WebResultCode {
    SUCCESS("success"),
    ERROR_INVALID_PARAMETER("error.invalid_parameter");

    private final String description;

    WebResultCode(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
