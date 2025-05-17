package spring.charityapp.blueprints;

import spring.charityapp.model.CharityAction;

import java.util.*;

public interface IUser {
    public void modifierProfile(Map<String, String> details );
    public void consultHistory();
    public void deleteAccount();


    public void addToHistory(CharityAction charityAction);
    public void removeFromHistory(CharityAction charityAction);


}