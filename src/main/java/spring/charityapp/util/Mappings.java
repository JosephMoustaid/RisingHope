package spring.charityapp.util;

public class Mappings {

    private Mappings(){}

    // == Constants ==
    public static final String HOME = "/home";

    // Auth
    public static final String LOGIN = "/auth/login";
    public static final String LOGOUT = "/auth/logout";
    public static final String USER_LOGIN = "/auth/login/user";
    public static final String ORGANIZATION_LOGIN = "/auth/login/organization";
    private static final String ADMIN_LOGIN = "/auth/login/admin";
    public static final String USER_REGISTER = "/auth/register/user";
    public static final String ORGANIZATION_REGISTER = "/auth/register/organization";
    public static final String USER_RESET_PASSWORD = "/auth/user/reset-password";
    public static final String ORGANIZATION_RESET_PASSWORD = "/auth/org/reset-password";

}
