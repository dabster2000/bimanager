package dk.trustworks.bimanager.jobs;

import dk.trustworks.bimanager.caches.CacheHandler;
import dk.trustworks.bimanager.caches.items.WorkItem;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.Project;
import dk.trustworks.bimanager.dto.ProjectYearEconomy;
import dk.trustworks.bimanager.dto.User;
import dk.trustworks.bimanager.dto.Work;
import dk.trustworks.bimanager.jobs.enums.Event;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

/**
 * Created by hans on 28/09/15.
 */
public class WorkItemMonthlyJob implements Job {

    private final int startYear = 2014;
    private final RestClient restClient = new RestClient();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("WorkItemMonthlyJob.execute");
        System.out.println("context.getJobDetail().getJobDataMap().getString(\"event\") = " + context.getJobDetail().getJobDataMap().getString("event"));

        loadWorkItems(context.getJobDetail().getJobDataMap().getString("event"));

        Map<String, ProjectYearEconomy> projectYearBudgetsMap = new HashMap<>();

        for (User user : restClient.getUsers()) {
            Map<String, ProjectYearEconomy> workByMonthProject = calculateWorkByMonthAndProject(getProjectBudgets(projectYearBudgetsMap, true, user.getUUID(), 2015), true, user.getUUID(), CacheHandler.workItems.values());
            CacheHandler.userWorkByMonthProject.put(user.getUUID(), workByMonthProject);
        }
    }

    private void loadWorkItems(String eventType) {
        if(eventType.equals(Event.HISTORIC.name())) loadHistoricWorkItems();
            else if (eventType.equals(Event.CURRENTMONTH.name())) loadCurrentMonthWorkItems();

    }

    private void loadCurrentMonthWorkItems() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();
        List<Work> registeredWorkByMonth = restClient.getRegisteredWorkByMonth(year, month);
        CacheHandler.workItems.putAll(year, WorkItem.createWorkItems(registeredWorkByMonth, projects));
    }

    private void loadHistoricWorkItems() {
        List<Project> projects = restClient.getProjectsAndTasksAndTaskWorkerConstraints();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = startYear; year <= currentYear; year++) {
            List<Work> registeredWorkByYear = restClient.getRegisteredWorkByYear(year);
            List<WorkItem> workItems = WorkItem.createWorkItems(registeredWorkByYear, projects);
            CacheHandler.workItems.replaceValues(year, workItems);
        }
    }

    private Map<String, ProjectYearEconomy> calculateWorkByMonthAndProject(Map<String, ProjectYearEconomy> projectYearBudgetsMap, boolean useRate, String userUUID, Collection<WorkItem> workItems) {
        for (WorkItem workItem : workItems) {
            if(!workItem.userUUID.equals(userUUID)) continue;
            ProjectYearEconomy projectYearEconomy;
            projectYearEconomy = projectYearBudgetsMap.get(workItem.projectUUID);
            if(projectYearBudgetsMap.get(workItem.projectUUID)==null) {
                projectYearBudgetsMap.put(workItem.projectUUID, new ProjectYearEconomy(workItem.projectUUID, "unknown"));
                projectYearEconomy = projectYearBudgetsMap.get(workItem.projectUUID);
            }
            projectYearEconomy.getActual()[workItem.month] = projectYearEconomy.getActual()[workItem.month]+(workItem.rate*workItem.hours);
        }
        return projectYearBudgetsMap;
    }

    private Map<String, ProjectYearEconomy> getProjectBudgets(Map<String, ProjectYearEconomy> projectYearBudgetsMap, boolean useRate, String userUUID, int year) {
        List<ProjectYearEconomy> projectBudgetsByUserAndYear = (useRate)?restClient.getProjectBudgetsByUserAndYear(userUUID, year):restClient.getProjectBudgetsByUserAndYearAndHours(userUUID, year);
        for (ProjectYearEconomy projectYearEconomy : projectBudgetsByUserAndYear) {
            projectYearBudgetsMap.put(projectYearEconomy.getProjectUUID(), projectYearEconomy);
        }
        return projectYearBudgetsMap;
    }
}
