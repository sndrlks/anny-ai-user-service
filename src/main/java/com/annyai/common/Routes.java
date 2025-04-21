package com.annyai.common;

public final class Routes {

    private Routes() {
        throw new IllegalStateException("Utility class");
    }

    public static final String API_V1 = "/api/v1";

    public static final class Auth {
        public static final String BASE = API_V1 + "/auth";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";
    }

    public static final class User {
        public static final String BASE = API_V1 + "/users";
        public static final String REGISTER = "/register";
    }

}
