package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 19/05/15.
 */
public class ProjectBudgetService extends DefaultLocalService {

    private static final Logger log = LogManager.getLogger(ProjectBudgetService.class);

    public ProjectBudgetService() {}

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
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        try {
            RestClient restClient = new RestClient();
            List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();

            Map<String, ProjectYearEconomy> projectYearBudgetsMap = new HashMap<>();
            for (ProjectYearEconomy projectYearEconomy : restClient.getProjectBudgetsByYear(year)) {
                projectYearBudgetsMap.put(projectYearEconomy.getProjectUUID(), projectYearEconomy);
            }
            log.debug("size: "+projectYearBudgetsMap.values().size());


            long allWorkTimer = System.currentTimeMillis();
            List<Work> allWork = restClient.getRegisteredWorkByYear(year);
            log.debug("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));

            for (Work work : allWork) {
                for (Project project : projects) {
                    for (Task task : project.getTasks()) {
                        if (work.getTaskUUID().equals(task.getUUID())) {
                            for (TaskWorkerConstraint taskWorkerConstraint : task.getTaskWorkerConstraints()) {
                                if (work.getUserUUID().equals(taskWorkerConstraint.getUserUUID())) {
                                    if(projectYearBudgetsMap.containsKey(project.getUUID())) {
                                        log.debug("project: {}", project);
                                        projectYearBudgetsMap.get(project.getUUID()).getActual()[work.getMonth()] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
                                        log.debug("budget: {}", projectYearBudgetsMap.get(project.getUUID()));
                                    } else {
                                        log.debug("new project: {}", project);
                                        ProjectYearEconomy economy = projectYearBudgetsMap.put(project.getUUID(), new ProjectYearEconomy(project.getUUID(), project.getName()));
                                        economy.getActual()[work.getMonth()] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            log.debug("Load all: {}", (System.currentTimeMillis() - allTimer));
            log.debug("size: "+projectYearBudgetsMap.values().size());
            ArrayList<ProjectYearEconomy> result = new ArrayList<>();
            result.addAll(projectYearBudgetsMap.values());
            return result;
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
    }

    public List<ProjectYearEconomy> findByUserAndYear(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByUserAndYear");
        log.debug("queryParameters = [" + queryParameters + "]");
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        String userUUID = queryParameters.get("useruuid").getFirst();
        List<ProjectYearEconomy> result = getProjectYearEconomies(allTimer, year, userUUID, true);
        if (result != null) return result;
        return null;
    }

    public List<ProjectYearEconomy> findByUserAndYearAndHours(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByUserAndYearAndHours");
        log.debug("queryParameters = [" + queryParameters + "]");
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        String userUUID = queryParameters.get("useruuid").getFirst();
        List<ProjectYearEconomy> result = getProjectYearEconomies(allTimer, year, userUUID, false);
        if (result != null) return result;
        return null;
    }

    private List<ProjectYearEconomy> getProjectYearEconomies(long allTimer, int year, String userUUID, boolean useRate) {
        try {
            RestClient restClient = new RestClient();
            List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();

            Map<String, ProjectYearEconomy> projectYearBudgetsMap = new HashMap<>();
            List<ProjectYearEconomy> projectBudgetsByUserAndYear = (useRate)?restClient.getProjectBudgetsByUserAndYear(userUUID, year):restClient.getProjectBudgetsByUserAndYearAndHours(userUUID, year);
            for (ProjectYearEconomy projectYearEconomy : projectBudgetsByUserAndYear) {
                projectYearBudgetsMap.put(projectYearEconomy.getProjectUUID(), projectYearEconomy);
            }
            log.debug("size: "+projectYearBudgetsMap.values().size());

            long allWorkTimer = System.currentTimeMillis();
            List<Work> allWork = restClient.getRegisteredWorkByUserAndYear(userUUID, year);
            log.debug("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));
            log.debug("Work loaded: {}", allWork.size());

            for (Work work : allWork) {
                for (Project project : projects) {
                    for (Task task : project.getTasks()) {
                        if (work.getTaskUUID().equals(task.getUUID())) {
                            for (TaskWorkerConstraint taskWorkerConstraint : task.getTaskWorkerConstraints()) {
                                if (work.getUserUUID().equals(taskWorkerConstraint.getUserUUID())) {
                                    if(projectYearBudgetsMap.containsKey(project.getUUID())) {
                                        if(useRate) projectYearBudgetsMap.get(project.getUUID()).getActual()[work.getMonth()] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
                                        else projectYearBudgetsMap.get(project.getUUID()).getActual()[work.getMonth()] += work.getWorkDuration();
                                    } else {
                                        ProjectYearEconomy economy = projectYearBudgetsMap.put(project.getUUID(), new ProjectYearEconomy(project.getUUID(), project.getName()));
                                        if(useRate) economy.getActual()[work.getMonth()] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
                                        else economy.getActual()[work.getMonth()] += work.getWorkDuration();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            log.debug("Load all: {}", (System.currentTimeMillis() - allTimer));
            log.debug("size: "+projectYearBudgetsMap.values().size());
            ArrayList<ProjectYearEconomy> result = new ArrayList<>();
            result.addAll(projectYearBudgetsMap.values());
            return result;
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
    }

    /*
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
            log.debug("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));

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
            log.debug("Ordering: {}", (System.currentTimeMillis() - timer));

            timer = System.currentTimeMillis();
            List<Project> projectsAndTasksAndTaskWorkerConstraints = restClient.getProjectsAndTasksAndTaskWorkerConstraints();
            log.debug("projectsAndTasksAndTaskWorkerConstraints: {}", (System.currentTimeMillis() - timer));

            StreamSupport.stream(projectsAndTasksAndTaskWorkerConstraints.spliterator(), true).map((project) -> {

                ProjectYearEconomy budgetSummary = new ProjectYearEconomy(project.getUUID(), project.getName());
                List<Task> tasks = project.getTasks();//restClient.getAllProjectTasks(project.getUUID());
                for (int month = 0; month < 12; month++) {
                    for (Task task : tasks) {
                        for (TaskWorkerConstraint taskWorkerConstraint : task.getTaskWorkerConstraints()) { // restClient.getTaskWorkerConstraint(task.getUUID()
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, 1, 0, 0);
                            if (year < 2016 && month < 6) calendar = Calendar.getInstance();
                            if (year >= Calendar.getInstance().get(Calendar.YEAR) && month > Calendar.getInstance().get(Calendar.MONTH))
                                calendar = Calendar.getInstance();
                            long specifiedTime = calendar.toInstant().toEpochMilli();
                            List<TaskWorkerConstraintBudget> budgets = restClient.getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskWorkerConstraint, month, year, specifiedTime);
                            List<Work> filteredWork = new ArrayList<Work>();
                            try {
                                filteredWork.addAll(orderedWork.get(taskWorkerConstraint.getUserUUID()).get(taskWorkerConstraint.getTaskUUID()).get(month));
                            } catch (Exception e) {
                            }
                            for (Work work : filteredWork) {
                                budgetSummary.getActual()[month] += (work.getWorkDuration() * taskWorkerConstraint.getPrice());
                            }
                            if (budgets.size() > 0) budgetSummary.getAmount()[month] += budgets.get(0).getBudget();
                        }
                    }
                }

                return budgetSummary;

            }).forEach(result -> projectYearBudgets.add(result));
            log.debug("Load all: {}", (System.currentTimeMillis() - allTimer));
            return projectYearBudgets;
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
    }
    */

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
