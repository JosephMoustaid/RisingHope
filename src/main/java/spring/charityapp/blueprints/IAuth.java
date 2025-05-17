package spring.charityapp.blueprints;

import java.util.*;

public interface IAuth {

    public boolean login(String email, String password);
    public void register(Map<String, String> details);
    public void logout();
    public void resetPassword(String email);

}