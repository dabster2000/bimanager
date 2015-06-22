package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.TaskWorkerConstraintBudget;
import dk.trustworks.bimanager.persistence.TaskBudgetRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 19/05/15.
 */
public class TaskBudgetService extends DefaultLocalService {

    private static final Logger log = LogManager.getLogger(TaskBudgetService.class);
    private TaskBudgetRepository taskBudgetRepository;

    public TaskBudgetService() { taskBudgetRepository = new TaskBudgetRepository(); }

    public Map<String, Object> findByTaskUUIDAndUserUUID(Map<String, Deque<String>> queryParameters) {
        log.debug("TaskBudgetService.findByTaskUUIDAndUserUUID");
        log.debug("queryParameters = [" + queryParameters + "]");
        String taskUUID = queryParameters.get("taskuuid").getFirst();
        String userUUID = queryParameters.get("useruuid").getFirst();
        RestClient restClient = new RestClient();
        double taskUserWorkHours = restClient.getTaskUserWorkHours(taskUUID, userUUID);
        double workerRate = restClient.getTaskWorkerRate(taskUUID, userUUID);
        double taskWorkerBudget = 0.0;
        for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : restClient.getBudgetsByTaskWorkerConstraintUUID(restClient.getTaskWorkerConstraint(taskUUID, userUUID))) {
            taskWorkerBudget += taskWorkerConstraintBudget.getBudget();
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("workhours", taskUserWorkHours);
        result.put("rate", workerRate);
        result.put("budget", taskWorkerBudget);
        result.put("remaining", taskWorkerBudget - (taskUserWorkHours * workerRate));
        System.out.println("result = " + result);
        return result;
    }

    @Override
    public GenericRepository getGenericRepository() {
        return null; // taskBudgetRepository;
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
