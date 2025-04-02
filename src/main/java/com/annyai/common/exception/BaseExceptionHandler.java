package com.annyai.common.exception;

import org.springframework.core.env.Environment;

import java.util.Arrays;

public abstract class BaseExceptionHandler {

    protected final Environment env;

    protected BaseExceptionHandler(Environment env) {
        this.env = env;
    }

    protected boolean isDevOrTest() {
        return Arrays.stream(env.getActiveProfiles())
                .anyMatch(profile -> profile.equals("dev") || profile.equals("test"));
    }
}
