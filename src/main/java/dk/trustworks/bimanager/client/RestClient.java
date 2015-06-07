package dk.trustworks.bimanager.client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.framework.network.Locator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    public double getTaskUserWorkHours(String taskuuid, String useruuid) {
        System.out.println("RestClient.getTaskUserWorkHours");
        System.out.println("taskuuid = [" + taskuuid + "], useruuid = [" + useruuid + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice")+"/api/works/calculateworkduration")
                    .queryString("taskuuid", taskuuid)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .asJson();
            return (double) jsonResponse.getBody().getObject().get("totalworkduration");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getProjectBudgetByTask(String taskUUID) {
        System.out.println("RestClient.getProjectBudgetByTask");
        System.out.println("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .queryString("projection", "projectuuid")
                    .header("accept", "application/json")
                    .asJson();
            return (double) jsonResponse.getBody().getObject().getJSONObject("project").get("budget");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTaskWorkerRate(String taskuuid, String useruuid) {
        System.out.println("RestClient.getTaskWorkerRate");
        System.out.println("taskuuid = [" + taskuuid + "], useruuid = [" + useruuid + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUIDAndUserUUID")
                    .queryString("taskuuid", taskuuid)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .asJson();
            return (double) jsonResponse.getBody().getObject().get("price");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Work> getRegisteredWorkByMonth(int month, int year) {
        System.out.println("RestClient.getRegisteredWorkByMonth");
        System.out.println("RestClient.getRegisteredWorkByMonth");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYearAndMonth")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {});
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TaskWorkerConstraint getTaskWorkerConstraint(String taskUUID, String userUUID) {
        System.out.println("RestClient.getTaskWorkerConstraint");
        System.out.println("taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUIDAndUserUUID")
                    .queryString("taskuuid", taskUUID)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<TaskWorkerConstraint>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraint> getTaskWorkerConstraint(String taskUUID) {
        System.out.println("RestClient.getTaskWorkerConstraint");
        System.out.println("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUID")
                    .queryString("taskuuid", taskUUID)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraint>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByTaskWorkerConstraintUUID(TaskWorkerConstraint taskWorkerConstraint) {
        System.out.println("RestClient.getBudgetsByTaskWorkerConstraintUUID");
        System.out.println("taskWorkerConstraint = " + taskWorkerConstraint);
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
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(TaskWorkerConstraint taskWorkerConstraint, int month, int year, Long datetime) {
        System.out.println("RestClient.getBudgetsByTaskWorkerConstraintUUIDAndDate");
        System.out.println("taskWorkerConstraint = [" + taskWorkerConstraint + "], month = [" + month + "], year = [" + year + "], datetime = [" + datetime + "]");
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
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public Map<String, String> getTaskProjectClient(String taskUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .queryString("projection", "projectuuid/clientuuid")
                    .header("accept", "application/json")
                    .asJson();
            System.out.println(Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .queryString("projection", "projectuuid,clientuuid")
                    .header("accept", "application/json")
                    .getUrl());
            System.out.println("getTaskWorkerRate: jsonResponse.getBody().getObject().toString() = " + jsonResponse.getBody().getObject().toString());

            HashMap<String, String> result = new HashMap<>();
            result.put("taskname", jsonResponse.getBody().getObject().get("name").toString());
            result.put("projectname", jsonResponse.getBody().getObject().getJSONObject("project").get("name").toString());
            result.put("clientname", jsonResponse.getBody().getObject().getJSONObject("project").getJSONObject("client").get("name").toString());
            return result;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Project getProjectByUUID(String projectUUID) {
        System.out.println("RestClient.getProjectByUUID");
        System.out.println("projectUUID = [" + projectUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects/" + projectUUID)
                    .header("accept", "application/json")
                    .asJson();
            System.out.println("jsonResponse = " + jsonResponse.getBody());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Project>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: project "+projectUUID, e);
        }
    }

    public Task getTaskByUUID(String taskUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Task>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: task "+taskUUID, e);
        }
    }

    public List<Task> getAllProjectTasks(String projectUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/search/findByProjectUUIDOrderByNameAsc")
                    .header("accept", "application/json")
                    .queryString("projectuuid", projectUUID)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Task>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: task by projectuuid "+projectUUID, e);
        }
    }

    public List<Project> getProjects() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Project>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: projects ", e);
        }
    }

    public List<User> getUsers() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: users ", e);
        }
    }
}
