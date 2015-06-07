package dk.trustworks.bimanager.handler;

import dk.trustworks.bimanager.service.ProjectBudgetService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;

/**
 * Created by hans on 16/03/15.
 */
public class ProjectBudgetHandler extends DefaultHandler {

    private final ProjectBudgetService projectBudgetService;

    public ProjectBudgetHandler() {
        super("projectbudget");
        this.projectBudgetService = new ProjectBudgetService();
    }

    @Override
    protected DefaultLocalService getService() {
        return projectBudgetService;
    }
}
