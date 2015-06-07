package dk.trustworks.bimanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hans on 12/05/15.
 */
@JsonIgnoreProperties({"created"})
public class TaskWorkerConstraintBudget {

    private String uuid;

    private double budget;

    private Integer month;

    private Integer year;

    @JsonProperty("taskworkerconstraintuuid")
    private String taskWorkerConstraintUUID;

    public TaskWorkerConstraintBudget() {
    }

    public TaskWorkerConstraintBudget(String uuid, double budget, int month, int year, String taskWorkerConstraintUUID, int version) {
        this.uuid = uuid;
        this.budget = budget;
        this.month = month;
        this.year = year;
        this.taskWorkerConstraintUUID = taskWorkerConstraintUUID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTaskWorkerConstraintUUID() {
        return taskWorkerConstraintUUID;
    }

    public void setTaskWorkerConstraintUUID(String taskWorkerConstraintUUID) {
        this.taskWorkerConstraintUUID = taskWorkerConstraintUUID;
    }


    @Override
    public String toString() {
        return "TaskWorkerConstraintBudget{" +
                "uuid='" + uuid + '\'' +
                ", budget=" + budget +
                ", month=" + month +
                ", year=" + year +
                ", taskWorkerConstraintUUID='" + taskWorkerConstraintUUID + '\'' +
                '}';
    }
}
