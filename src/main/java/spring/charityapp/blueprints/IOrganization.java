package spring.charityapp.blueprints;

import spring.charityapp.model.CharityAction;

/**
 *
 */
public interface IOrganization {

    public void manageProfile();
    public void addAction(CharityAction action);
    public void approveOrganization(String organizationId);

}