package com.mws.backend.framework.dto;

public enum WebResultCode {
    SUCCESS("success");

    private final String description;

    WebResultCode(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
