package com.mws.back_end.framework.dto;


public enum WebResultCode {
    SUCCESS("success"),
    ERROR_MISSING_MANDATORY_PARAMETER("error.missing_mandatory_parameter"),
    ERROR_INVALID_PARAMETER("error.invalid_parameter"),
    ERROR_AUTH("error.auth");

    private final String description;

    WebResultCode(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
