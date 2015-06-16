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
import java.time.Instant;
import java.time.LocalDateTime;
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
        List<ProjectYearEconomy> projectYearBudgets = new ArrayList<ProjectYearEconomy>();

        StreamSupport.stream(restClient.getProjects().spliterator(), true).map((project) -> {
            ProjectYearEconomy budgetSummary = new ProjectYearEconomy(project.getUUID(), project.getName());
            List<Task> tasks = restClient.getAllProjectTasks(project.getUUID());
            for (int month = 0; month < 12; month++) {
                for (Task task : tasks) {
                    for (TaskWorkerConstraint taskWorkerConstraint : restClient.getTaskWorkerConstraint(task.getUUID())) {
                        long specifiedTime = Instant.from(LocalDateTime.of(year, month, 1, 0, 0)).toEpochMilli();
                        List<TaskWorkerConstraintBudget> budgets = restClient.getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskWorkerConstraint, month, year, specifiedTime);
                        if (budgets.size() > 0) budgetSummary.getAmount()[month] += budgets.get(0).getBudget();
                    }

                }
            }
/*
            restClient.getBudgetsByTaskWorkerConstraintUUIDAndDate()
            for (int month = 0; month < 12; month++) {
                List<MonthBudget> monthBudgetHistory = monthBudgetHistoryRepository.findByProjectUUIDAndYearAndMonth(project.getUUID(), year, month);
                if (monthBudgetHistory.size() == 0) {
                    List<MonthBudget> monthBudget = monthBudgetRepository.findByProjectUUIDAndYearAndMonth(project.getUUID(), year, month);
                    if (monthBudget.size() == 0) continue;
                    budgetSummary.getAmount()[month] = monthBudget.get(0).getBudget();
                } else {
                    budgetSummary.getAmount()[month] = monthBudgetHistory.get(0).getBudget();
                }

            }*/
            return budgetSummary;

        }).forEach(result -> projectYearBudgets.add(result));
        return projectYearBudgets;
    }

    @Override
    public GenericRepository getGenericRepository() {
        return taskBudgetRepository;
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
