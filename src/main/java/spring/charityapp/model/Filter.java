package spring.charityapp.model;

import spring.charityapp.common.ActionState;
import spring.charityapp.common.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Filter  {
    public List<CharityAction> filterByName(List<CharityAction> actions, String query) {
        ArrayList<CharityAction> filtered = actions.stream().
                filter(action -> action.getTitle().equals(query)).
                collect(Collectors.toCollection(ArrayList<CharityAction>::new));
        return filtered ;
    }
    public List<CharityAction> filterByOldest(List<CharityAction> actions) {
        return actions.stream()
                .sorted((act1, act2) -> act1.getStartDate().compareTo(act2.getStartDate())) // Ascending order
                .collect(Collectors.toList()); // Collecting to a List
    }
    public List<CharityAction> filterByMostRecent(List<CharityAction> actions) {
        return actions.stream()
                .sorted((act1, act2) -> act2.getStartDate().compareTo(act1.getStartDate())) // Descending order
                .collect(Collectors.toList()); // Collecting to a List (no need for ArrayList explicitly)
    }
    public List<CharityAction> filterByActionState(List<CharityAction> actions, ActionState state) {
        return actions.stream()
                .filter(act -> act.getActionState().equals(state))
                .collect(Collectors.toList());
    }
    public List<CharityAction> filterByCategory(List<CharityAction> actions, Category category) {
        return actions.stream()
                .filter(act -> act.getCategory().equals(category))
                .collect(Collectors.toList());
    }
}

