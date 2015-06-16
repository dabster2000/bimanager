package dk.trustworks.bimanager.handler;

import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.TaskWorkerConstraint;
import dk.trustworks.bimanager.dto.TaskWorkerConstraintBudget;
import dk.trustworks.bimanager.dto.Work;
import dk.trustworks.bimanager.service.ProjectBudgetService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import io.undertow.server.HttpServerExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by hans on 16/03/15.
 */
public class ProjectBudgetHandler extends DefaultHandler {

    private static final Logger log = LogManager.getLogger(ProjectBudgetHandler.class);
    private final ProjectBudgetService projectBudgetService;

    public ProjectBudgetHandler() {
        super("projectbudget");
        this.projectBudgetService = new ProjectBudgetService();
        addCommand("budgetcleanup");
    }

    public void budgetcleanup (HttpServerExchange exchange, String[] params) {
        log.entry(exchange, params);
        int month = Integer.parseInt(exchange.getQueryParameters().get("month").getFirst());
        log.debug("month = " + month);
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        log.debug("year = " + year);

        RestClient restClient = new RestClient();
        List<TaskWorkerConstraintBudget> workBudgets = new ArrayList<>();
        Map<String, TaskWorkerConstraintBudget> workBudgetMap = new HashMap<>();
        for (Work work : restClient.getRegisteredWorkByMonth(month, year)) {
            TaskWorkerConstraint taskWorkerConstraint = restClient.getTaskWorkerConstraint(work.getTaskUUID(), work.getUserUUID());
            log.debug("work ("+taskWorkerConstraint.getUUID()+") = " + work);
            log.debug("taskWorkerConstraint = " + taskWorkerConstraint);
            if(!workBudgetMap.containsKey(taskWorkerConstraint.getUUID())) {
                workBudgetMap.put(taskWorkerConstraint.getUUID(), new TaskWorkerConstraintBudget(0.0, month, "", UUID.randomUUID().toString(), year));
            }
            TaskWorkerConstraintBudget currentBudget = workBudgetMap.get(taskWorkerConstraint.getUUID());
            if(taskWorkerConstraint.getUUID()==null) log.error("LOG00180: "+taskWorkerConstraint);
            currentBudget.setBudget(currentBudget.getBudget() + (work.getWorkDuration() * taskWorkerConstraint.getPrice()));
            currentBudget.setTaskWorkerConstraintUUID(taskWorkerConstraint.getUUID());
            currentBudget.setTaskWorkerConstraint(taskWorkerConstraint);
            log.debug("currentBudget ("+taskWorkerConstraint.getUUID()+") = " + currentBudget);
        }
        workBudgets.addAll(workBudgetMap.values());

        List<TaskWorkerConstraintBudget> actualBudgets = restClient.getBudgetsByMonthAndYear(month, year);
        List<TaskWorkerConstraintBudget> newBudgets = new ArrayList<>();
        List<TaskWorkerConstraintBudget> newRemoveList = new ArrayList<>();
        for (TaskWorkerConstraintBudget newBudget : workBudgetMap.values()) {
            List<TaskWorkerConstraintBudget> actualRemoveList = new ArrayList<>();
            for (TaskWorkerConstraintBudget actualBudget : actualBudgets) {
                if(actualBudget.getTaskWorkerConstraintUUID().equals(newBudget.getTaskWorkerConstraintUUID()) &&
                                actualBudget.getYear() == newBudget.getYear() &&
                                actualBudget.getMonth() == newBudget.getMonth()
                                ) {
                    actualBudget.setBudget(newBudget.getBudget());
                    newBudgets.add(actualBudget);
                    actualRemoveList.add(actualBudget);
                    newRemoveList.add(newBudget);
                }
            }
            actualBudgets.removeAll(actualRemoveList);
        }
        workBudgets.removeAll(newRemoveList);
        newBudgets.addAll(workBudgets);

        for (TaskWorkerConstraintBudget actualBudget : actualBudgets) {
            actualBudget.setBudget(0.0);
            newBudgets.add(actualBudget);
        }

        for (TaskWorkerConstraintBudget newBudget : newBudgets) {
            restClient.postTaskBudget(newBudget);
        }

        log.exit();
    }

    @Override
    protected DefaultLocalService getService() {
        return projectBudgetService;
    }
}
