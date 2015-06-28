package dk.trustworks.bimanager.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.framework.network.Locator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    private static final Logger log = LogManager.getLogger(RestClient.class);

    public double getTaskUserWorkHours(String taskuuid, String useruuid) {
        log.entry(taskuuid, useruuid);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice")+"/api/works/calculateworkduration")
                    .queryString("taskuuid", taskuuid)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .asJson();
            log.exit((double) jsonResponse.getBody().getObject().get("totalworkduration"));
            return (double) jsonResponse.getBody().getObject().get("totalworkduration");
        } catch (UnirestException e) {
            log.catching(e);
        }
        log.exit(0.0);
        return 0.0;
    }

    public double getProjectBudgetByTask(String taskUUID) {
        log.entry(taskUUID);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .queryString("projection", "projectuuid")
                    .header("accept", "application/json")
                    .asJson();
            log.exit((double) jsonResponse.getBody().getObject().getJSONObject("project").get("budget"));
            return (double) jsonResponse.getBody().getObject().getJSONObject("project").get("budget");
        } catch (UnirestException e) {
            log.catching(e);
        }
        log.exit(0.0);
        return 0.0;
    }

    public double getTaskWorkerRate(String taskuuid, String useruuid) {
        log.debug("RestClient.getTaskWorkerRate");
        log.debug("taskuuid = [" + taskuuid + "], useruuid = [" + useruuid + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUIDAndUserUUID")
                    .queryString("taskuuid", taskuuid)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .asJson();
            return (double) jsonResponse.getBody().getObject().get("price");
        } catch (UnirestException e) {
            log.catching(e);
        }
        return 0.0;
    }

    public List<Work> getRegisteredWorkByMonth(int month, int year) {
        log.entry(month, year);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYearAndMonth")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
            });
            log.exit(result);
            return result;
        } catch (UnirestException | IOException e) {
            log.catching(e);
        }
        log.exit(new ArrayList<>());
        return new ArrayList<>();
    }

    public List<Work> getRegisteredWorkByYear(int year) {
        log.entry(year);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYear")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {});
            log.exit(result);
            return result;
        } catch (UnirestException | IOException e) {
            log.catching(e);
        }
        log.exit(new ArrayList<>());
        return new ArrayList<>();
    }

    public TaskWorkerConstraint getTaskWorkerConstraint(String taskUUID, String userUUID) {
        log.entry(taskUUID, userUUID);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUIDAndUserUUID")
                    .queryString("taskuuid", taskUUID)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            TaskWorkerConstraint result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<TaskWorkerConstraint>() {});
            log.exit(result);
            return result;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraint> getTaskWorkerConstraint(String taskUUID) {
        log.debug("RestClient.getTaskWorkerConstraint");
        log.debug("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUID")
                    .queryString("taskuuid", taskUUID)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraint>>() {});
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByTaskWorkerConstraintUUID(TaskWorkerConstraint taskWorkerConstraint) {
        log.debug("RestClient.getBudgetsByTaskWorkerConstraintUUID");
        log.debug("taskWorkerConstraint = [" + taskWorkerConstraint + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByTaskWorkerConstraintUUID")
                    .queryString("taskworkerconstraintuuid", taskWorkerConstraint.getUUID())
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {});
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByMonthAndYear(int month, int year) {
        log.debug("RestClient.getBudgetsByMonthAndYear");
        log.debug("month = [" + month + "], year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByMonthAndYear")
                    .queryString("month", month)
                    .queryString("year", year)
                    //.queryString("projection", "taskworkerconstraintuuid")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {});
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(TaskWorkerConstraint taskWorkerConstraint, int month, int year, Long datetime) {
        log.debug("RestClient.getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        log.debug("taskWorkerConstraint = [" + taskWorkerConstraint + "], month = [" + month + "], year = [" + year + "], datetime = [" + datetime + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate")
                    .queryString("taskworkerconstraintuuid", taskWorkerConstraint.getUUID())
                    .queryString("month", month)
                    .queryString("year", year)
                    .queryString("datetime", datetime)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {});
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public Map<String, String> getTaskProjectClient(String taskUUID) {
        log.debug("RestClient.getTaskProjectClient");
        log.debug("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .queryString("projection", "projectuuid/clientuuid")
                    .header("accept", "application/json")
                    .asJson();
            log.debug("getTaskWorkerRate: jsonResponse.getBody().getObject().toString() = " + jsonResponse.getBody().getObject().toString());

            HashMap<String, String> result = new HashMap<>();
            result.put("taskname", jsonResponse.getBody().getObject().get("name").toString());
            result.put("projectname", jsonResponse.getBody().getObject().getJSONObject("project").get("name").toString());
            result.put("clientname", jsonResponse.getBody().getObject().getJSONObject("project").getJSONObject("client").get("name").toString());
            return result;
        } catch (UnirestException e) {
            log.catching(e);
        }
        log.info("LOG00080: Returning null from getTaskProjectClient");
        return null;
    }

    public Project getProjectByUUID(String projectUUID) {
        log.debug("RestClient.getProjectByUUID");
        log.debug("projectUUID = [" + projectUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects/" + projectUUID)
                    .header("accept", "application/json")
                    .asJson();
            log.debug("jsonResponse = " + jsonResponse.getBody());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Project>() {});
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: project "+projectUUID, e);
        }
    }

    public Task getTaskByUUID(String taskUUID) {
        log.debug("RestClient.getTaskByUUID");
        log.debug("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Task>() {});
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: task "+taskUUID, e);
        }
    }

    public List<Task> getAllProjectTasks(String projectUUID) {
        log.debug("RestClient.getAllProjectTasks");
        log.debug("projectUUID = [" + projectUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/search/findByProjectUUIDOrderByNameAsc")
                    .header("accept", "application/json")
                    .queryString("projectuuid", projectUUID)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Task>>() {});
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: task by projectuuid "+projectUUID, e);
        }
    }

    public List<Project> getProjects() {
        log.debug("RestClient.getProjects");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Project>>() {});
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: projects ", e);
        }
    }

    public List<Project> getProjectsAndTasksAndTaskWorkerConstraints() {
        log.debug("RestClient.getProjects");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects")
                    .queryString("children", "taskuuid/taskworkerconstraintuuid")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Project>>() {});
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: projects ", e);
        }
    }

    public List<User> getUsers() {
        log.debug("RestClient.getUsers");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {});
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: users ", e);
        }
    }

    public void postTaskBudget(TaskWorkerConstraintBudget taskWorkerConstraintBudget) {
        log.debug("RestClient.postTaskBudget");
        log.debug("taskWorkerConstraintBudget = [" + taskWorkerConstraintBudget + "]");
        try {
            Unirest.post(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets")
                    .header("accept", "application/json")
                    .body(new ObjectMapper().writeValueAsString(taskWorkerConstraintBudget))
                    .asJson();
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke skrive: taskWorkerConstraintBudget "+taskWorkerConstraintBudget, e);
        }
    }
}
