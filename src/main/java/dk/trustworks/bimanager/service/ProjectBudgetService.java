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

    public List<ProjectYearEconomy> findByYearAndMonth(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByYearAndMonth");
        log.debug("queryParameters = [" + queryParameters + "]");
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        int month = Integer.parseInt(queryParameters.get("month").getFirst());
        try {
            RestClient restClient = new RestClient();
            List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();

            Map<String, ProjectYearEconomy> projectYearBudgetsMap = new HashMap<>();

            long allWorkTimer = System.currentTimeMillis();
            List<Work> allWork = restClient.getRegisteredWorkByMonth(year, month);
            log.debug("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));
            log.debug("Work found: "+allWork.size());

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
                                        ProjectYearEconomy economy = new ProjectYearEconomy(project.getUUID(), project.getName());
                                        projectYearBudgetsMap.put(project.getUUID(), economy);
                                        System.out.println("economy = " + economy);
                                        System.out.println("taskWorkerConstraint = " + taskWorkerConstraint);
                                        System.out.println("work = " + work);
                                        economy.getActual()[month] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
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

    public List<Budget> findItByYear(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByUserAndYear");
        log.debug("queryParameters = [" + queryParameters + "]");
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());

        List<Budget> result2 = new ArrayList<>();
        for (User user : new RestClient().getUsers()) {
            log.debug(user.getFirstname());
            String userUUID = user.getUUID();
            List<ProjectYearEconomy> result = getProjectYearEconomies(allTimer, year, userUUID, true);
            double earned = 0.0;
            for (ProjectYearEconomy projectYearEconomy : result) {
                for (double v : projectYearEconomy.getActual()) {
                    earned += v;
                }
            }

            List<ProjectYearEconomy> result3 = getProjectYearEconomies(allTimer, year, userUUID, false);
            double hours = 0.0;
            for (ProjectYearEconomy projectYearEconomy : result3) {
                if(projectYearEconomy.getProjectUUID().equals("fdfbb1a1-bbae-48a1-955d-e681153d6731")) continue;
                for (double v : projectYearEconomy.getActual()) {
                    hours += v;
                }
            }
            result2.add(new Budget(user.getFirstname(), 0.0, hours, earned));
        }
        return result2;
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

    public List<ProjectYearEconomy> getProjectYearEconomies(long allTimer, int year, String userUUID, boolean useRate) {
        log.debug("ProjectBudgetService.getProjectYearEconomies");
        long time = System.currentTimeMillis();
        try {
            RestClient restClient = new RestClient();

            Collection<Project> projects;
            //projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();
            //projects = restClient.getProjects();

            Map<String, ProjectYearEconomy> projectYearBudgetsMap = new HashMap<>();
            List<ProjectYearEconomy> projectBudgetsByUserAndYear = (useRate)?restClient.getProjectBudgetsByUserAndYear(userUUID, year):restClient.getProjectBudgetsByUserAndYearAndHours(userUUID, year);
            for (ProjectYearEconomy projectYearEconomy : projectBudgetsByUserAndYear) {
                projectYearBudgetsMap.put(projectYearEconomy.getProjectUUID(), projectYearEconomy);
            }
            log.debug("size: "+projectYearBudgetsMap.values().size());
/*
            for (WorkItem workItem : BiApplication.workItems.values()) {
                if(!workItem.userUUID.equals(userUUID)) continue;
                economyByMonth projectYearEconomy;
                projectYearEconomy = projectYearBudgetsMap.get(workItem.projectUUID);
                if(projectYearBudgetsMap.get(workItem.projectUUID)==null) {
                    projectYearBudgetsMap.put(workItem.projectUUID, new economyByMonth(workItem.projectUUID, "unknown"));
                    projectYearEconomy = projectYearBudgetsMap.get(workItem.projectUUID);
                }
                projectYearEconomy.getActual()[workItem.month] = projectYearEconomy.getActual()[workItem.month]+(workItem.rate*workItem.hours);
            }

            for (Project project : projects) {
                if(projectYearBudgetsMap.containsKey(project.getUUID())) {
                    for (Month month : Month.values()) {
                        double sumIncome = 0.0;
                        for (WorkItem workItem : BiApplication.workItems.values()) {
                            if(workItem.projectUUID.equals(project.getUUID()) &&
                                    workItem.month == month.getValue()-1 &&
                                    workItem.year == 2015 &&
                                    workItem.userUUID.equals(userUUID)
                                    ) sumIncome += workItem.hours * workItem.rate;
                        }
                        economyByMonth projectYearEconomy = projectYearBudgetsMap.get(project.getUUID());
                        projectYearEconomy.getActual()[month.getValue()-1] = sumIncome;
                    }
                } else {
                    economyByMonth projectYearEconomy = projectYearBudgetsMap.put(project.getUUID(), new economyByMonth(project.getUUID(), project.getName()));
                    for (Month month : Month.values()) {
                        double sumIncome = 0.0;
                        for (WorkItem workItem : BiApplication.workItems.values()) {
                            if(workItem.projectUUID.equals(project.getUUID()) &&
                                    workItem.month == month.getValue()-1 &&
                                    workItem.year == 2015 &&
                                    workItem.userUUID.equals(userUUID)
                                    ) sumIncome += workItem.hours * workItem.rate;
                        }
                        projectYearEconomy.getActual()[month.getValue()-1] = sumIncome;
                    }
                }
            }
            */

/*
            long allWorkTimer = System.currentTimeMillis();
            List<Work> allWork = restClient.getRegisteredWorkByUserAndYear(userUUID, year);
            log.debug("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));
            log.debug("Work loaded: {}", allWork.size());

            for (Work work : allWork) {
                for (Project project : projects) {
                    IMap<String, Task> tasks = hzInstance.getMap("task");
                    for (Task task : tasks.values(Predicates.equal("uuid", project.getUUID()))) {
                        if (work.getTaskUUID().equals(task.getUUID())) {
                            IMap<String, TaskWorkerConstraint> taskWorkerConstraints = hzInstance.getMap("taskworkerconstraints");
                            for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraints.values(Predicates.equal("uuid", task.getUUID()))) {
                                if (work.getUserUUID().equals(taskWorkerConstraint.getUserUUID())) {
                                    if(projectYearBudgetsMap.containsKey(project.getUUID())) {
                                        if(useRate) projectYearBudgetsMap.get(project.getUUID()).getActual()[work.getMonth()] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
                                        else projectYearBudgetsMap.get(project.getUUID()).getActual()[work.getMonth()] += work.getWorkDuration();
                                    } else {
                                        economyByMonth economy = projectYearBudgetsMap.put(project.getUUID(), new economyByMonth(project.getUUID(), project.getName()));
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
*/
            ArrayList<ProjectYearEconomy> result = new ArrayList<>();
            result.addAll(projectYearBudgetsMap.values());

            System.out.println("System.currentTimeMillis() - time = " + (System.currentTimeMillis() - time));
            return result;
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
    }

    /*
    public List<economyByMonth> findByYear(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByYear");
        log.debug("queryParameters = [" + queryParameters + "]");
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        try {
            RestClient restClient = new RestClient();

            List<economyByMonth> projectYearBudgets = new ArrayList<>();
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

                economyByMonth budgetSummary = new economyByMonth(project.getUUID(), project.getName());
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
