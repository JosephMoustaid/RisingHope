package spring.charityapp.blueprints;

import spring.charityapp.model.Transaction;
import spring.charityapp.common.ActionState;
import spring.charityapp.model.User;

import java.util.*;

/**
 *
 */
public interface ICharityAction {

    public void update(Map<String, String> details);
    public void archive(ActionState state);
    public void consult();
    public void donate(User user, Transaction transaction);
    public String getTotalDonations();

}