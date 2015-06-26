package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.bimanager.persistence.TaskBudgetRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by hans on 19/05/15.
 */
public class ProjectBudgetService extends DefaultLocalService {

    private static final Logger log = LogManager.getLogger(ProjectBudgetService.class);
    private TaskBudgetRepository taskBudgetRepository;

    public ProjectBudgetService() { taskBudgetRepository = new TaskBudgetRepository(); }

    public Map<String, Object> findByProjectUUID(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByProjectUUID");
        log.debug("queryParameters = [" + queryParameters + "]");
        String projectUUID = queryParameters.get("projectuuid").getFirst();
        RestClient restClient = new RestClient();
        Project project = restClient.getProjectByUUID(projectUUID);
        List<Task> tasks = restClient.getAllProjectTasks(projectUUID);
        double assignedBudget = 0.0;
        for (Task task : tasks) {
            for (TaskWorkerConstraint taskWorkerConstraint : restClient.getTaskWorkerConstraint(task.getUUID())) {
                for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : restClient.getBudgetsByTaskWorkerConstraintUUID(taskWorkerConstraint)) {
                    assignedBudget += taskWorkerConstraintBudget.getBudget();
                }
            }
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("projectbudget", project.getBudget());
        result.put("assignedbudget", assignedBudget);
        return result;
    }

    public List<ProjectYearEconomy> findByYear(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByYear");
        log.debug("queryParameters = [" + queryParameters + "]");
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        log.debug("year = " + year);
        RestClient restClient = new RestClient();

        List<ProjectYearEconomy> projectYearBudgets = new ArrayList<>();
        List<Work> allWork = restClient.getRegisteredWorkByYear(year);

        StreamSupport.stream(restClient.getProjects().spliterator(), true).map((project) -> {
            log.trace("Stream: "+project.getName());
            ProjectYearEconomy budgetSummary = new ProjectYearEconomy(project.getUUID(), project.getName());
            List<Task> tasks = restClient.getAllProjectTasks(project.getUUID());
            for (int month = 0; month < 12; month++) {
                for (Task task : tasks) {
                    for (TaskWorkerConstraint taskWorkerConstraint : restClient.getTaskWorkerConstraint(task.getUUID())) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, 1, 0, 0);
                        long specifiedTime = calendar.toInstant().toEpochMilli();
                        List<TaskWorkerConstraintBudget> budgets = restClient.getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskWorkerConstraint, month, year, specifiedTime);
                        for (Work work : allWork) {
                            if(work.getTaskUUID().equals(taskWorkerConstraint.getTaskUUID()) &&
                                    work.getUserUUID().equals(taskWorkerConstraint.getUserUUID()) &&
                                    work.getMonth() == month) {
                                log.trace("month: "+month);
                                log.debug("taskWorkerConstraint = " + taskWorkerConstraint);
                                log.debug("specifiedTime = " + calendar);
                                log.debug("budgets.size() = " + budgets.size());
                                budgetSummary.getActual()[month] += (work.getWorkDuration() * taskWorkerConstraint.getPrice());
                            }
                        }
                        log.debug("budgets.size() = " + budgets.size());
                        if (budgets.size() > 0) budgetSummary.getAmount()[month] += budgets.get(0).getBudget();
                    }
                }
            }
            System.out.println("budgetSummary = " + budgetSummary);
            return budgetSummary;

        }).forEach(result -> projectYearBudgets.add(result));
        return projectYearBudgets;
    }

    @Override
    public GenericRepository getGenericRepository() {
        return null; //taskBudgetRepository;
    }

    @Override
    public String getResourcePath() {
        return "taskbudgets";
    }

    @Override
    public void create(JsonNode clientJsonNode) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update(JsonNode clientJsonNode, String uuid) throws SQLException {
        throw new RuntimeException("Not implemented");
    }
}
