package dk.trustworks.bimanager.handler;

import dk.trustworks.bimanager.service.TaskBudgetService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;

/**
 * Created by hans on 16/03/15.
 */
public class TaskBudgetHandler extends DefaultHandler {

    private final TaskBudgetService taskBudgetService;

    public TaskBudgetHandler() {
        super("taskbudget");
        this.taskBudgetService = new TaskBudgetService();
    }

    @Override
    protected DefaultLocalService getService() {
        return taskBudgetService;
    }
}
