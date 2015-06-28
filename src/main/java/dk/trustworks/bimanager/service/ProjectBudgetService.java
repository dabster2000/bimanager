package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Predicate;
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

    Predicate<Work> taskUUIDEqualsTo(final String taskUUID) {
        return dataPoint -> dataPoint.getTaskUUID().equals(taskUUID);
    }

    public List<ProjectYearEconomy> findByYear(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByYear");
        log.debug("queryParameters = [" + queryParameters + "]");
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        try {
            RestClient restClient = new RestClient();

            List<ProjectYearEconomy> projectYearBudgets = new ArrayList<>();
            long allWorkTimer = System.currentTimeMillis();
            List<Work> allWork = restClient.getRegisteredWorkByYear(year);
            log.info("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));

            Map<String, Map<String, Map<Integer, List<Work>>>> orderedWork = new HashMap();

            long timer = System.currentTimeMillis();
            for (Work work : allWork) {
                if (!orderedWork.containsKey(work.getUserUUID())) orderedWork.put(work.getUserUUID(), new HashMap<>());
                if (!orderedWork.get(work.getUserUUID()).containsKey(work.getTaskUUID()))
                    orderedWork.get(work.getUserUUID()).put(work.getTaskUUID(), new HashMap<>());
                if (!orderedWork.get(work.getUserUUID()).get(work.getTaskUUID()).containsKey(work.getMonth()))
                    orderedWork.get(work.getUserUUID()).get(work.getTaskUUID()).put(work.getMonth(), new ArrayList<>());
                orderedWork.get(work.getUserUUID()).get(work.getTaskUUID()).get(work.getMonth()).add(work);
            }
            log.info("Ordering: {}", (System.currentTimeMillis()-timer));


            StreamSupport.stream(restClient.getProjects().spliterator(), true).map((project) -> {

                ProjectYearEconomy budgetSummary = new ProjectYearEconomy(project.getUUID(), project.getName());
                List<Task> tasks = restClient.getAllProjectTasks(project.getUUID());
                for (int month = 0; month < 12; month++) {
                    for (Task task : tasks) {
                        for (TaskWorkerConstraint taskWorkerConstraint : restClient.getTaskWorkerConstraint(task.getUUID())) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, 1, 0, 0);
                            if (year < 2016 && month < 6) calendar = Calendar.getInstance();
                            if (year >= Calendar.getInstance().get(Calendar.YEAR) && month > Calendar.getInstance().get(Calendar.MONTH))
                                calendar = Calendar.getInstance();
                            long specifiedTime = calendar.toInstant().toEpochMilli();
                            //long budgetsTimer = System.currentTimeMillis();
                            List<TaskWorkerConstraintBudget> budgets = restClient.getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskWorkerConstraint, month, year, specifiedTime);
                            //log.info("Pulling: {}", (System.currentTimeMillis() - budgetsTimer));
                            //Collection<Work> filteredWork = Collections2.filter(allWork, taskUUIDEqualsTo(taskWorkerConstraint.getTaskUUID()));
                            List<Work> filteredWork = new ArrayList<Work>();
                            try {
                                filteredWork.addAll(orderedWork.get(taskWorkerConstraint.getUserUUID()).get(taskWorkerConstraint.getTaskUUID()).get(month));
                            } catch (Exception e) {}
                            log.debug("size: ?", filteredWork.size());
                            for (Work work : filteredWork) {
                            /*
                            if(work.getTaskUUID().equals(taskWorkerConstraint.getTaskUUID()) &&
                                    work.getUserUUID().equals(taskWorkerConstraint.getUserUUID()) &&
                                    work.getMonth() == month) {*/
                                budgetSummary.getActual()[month] += (work.getWorkDuration() * taskWorkerConstraint.getPrice());
                                //}
                            }
                            if (budgets.size() > 0) budgetSummary.getAmount()[month] += budgets.get(0).getBudget();
                        }
                    }
                }

                return budgetSummary;

            }).forEach(result -> projectYearBudgets.add(result));
            log.info("Load all: {}", (System.currentTimeMillis() - allTimer));
            return projectYearBudgets;
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
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
