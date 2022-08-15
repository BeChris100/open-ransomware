package com.bechris100.open_ransomware.config;

public class ConfigurationNotFoundException extends RuntimeException {

    public ConfigurationNotFoundException() {
        super();
    }

    public ConfigurationNotFoundException(String message) {
        super(message);
    }

    public ConfigurationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ConfigurationNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
