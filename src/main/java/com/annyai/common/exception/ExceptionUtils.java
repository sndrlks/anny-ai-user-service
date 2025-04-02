package com.annyai.common.exception;

import org.slf4j.Logger;


public class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static String maskSensitive(String msg) {
        if (msg == null) return null;
        return msg
                .replaceAll("(?i)password=[^&\\s]+", "password=****")
                .replaceAll("(?i)token=[^&\\s]+", "token=****")
                .replaceAll("(?i)jwt[^\\s]*", "jwt=****");
    }

    public static void logSafeError(Logger log, Throwable ex, String contextMessage, boolean isDev) {
        Throwable root = getRootCause(ex);
        if (isDev) {
            log.error("{}: {}", contextMessage, root.getMessage(), ex);
        } else {
            log.error("{}: {}", contextMessage, maskSensitive(root.getMessage()));
        }
    }
}