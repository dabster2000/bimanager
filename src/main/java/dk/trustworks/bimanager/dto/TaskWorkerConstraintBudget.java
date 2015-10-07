package dk.trustworks.bimanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by hans on 12/05/15.
 */
@JsonIgnoreProperties({"created"})
public class TaskWorkerConstraintBudget implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid;

    private double budget;

    private Integer month;

    private Integer year;

    @JsonProperty("taskworkerconstraintuuid")
    private String taskWorkerConstraintUUID;

    @JsonProperty("taskworkerconstraint")
    private TaskWorkerConstraint taskWorkerConstraint;

    public TaskWorkerConstraintBudget() {
    }

    public TaskWorkerConstraintBudget(double budget, Integer month, String taskWorkerConstraintUUID, String uuid, Integer year) {
        this.budget = budget;
        this.month = month;
        this.taskWorkerConstraintUUID = taskWorkerConstraintUUID;
        this.uuid = uuid;
        this.year = year;
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

    public TaskWorkerConstraint getTaskWorkerConstraint() {
        return taskWorkerConstraint;
    }

    public void setTaskWorkerConstraint(TaskWorkerConstraint taskWorkerConstraint) {
        this.taskWorkerConstraint = taskWorkerConstraint;
    }
}
