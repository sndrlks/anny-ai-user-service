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

    public static void logSafeError(Logger log, String contextMessage, Throwable ex, boolean isDev) {
        if (isDev) {
            log.error("{}: {}", contextMessage, ex.getMessage(), ex);
        } else {
            log.error("{}: {}", contextMessage, maskSensitive(ex.getMessage()));
        }
    }

    public static String getSafeMessage(Throwable ex, boolean isDev) {
        String msg = ex.getMessage();
        return isDev ? msg : maskSensitive(msg);
    }
}