package dk.trustworks.bimanager.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TaskWorkerConstraint implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("uuid")
    private String UUID;

    private double price;

    @JsonProperty("useruuid")
	private String userUUID;

    @JsonProperty("taskuuid")
	private String taskUUID;

    public TaskWorkerConstraint() {
    }

    public TaskWorkerConstraint(String UUID, double price) {
        this.UUID = UUID;
        this.price = price;
    }

    public TaskWorkerConstraint(String UUID, double price, String userUUID, String taskUUID) {
        this.UUID = UUID;
        this.price = price;
        this.userUUID = userUUID;
        this.taskUUID = taskUUID;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    @Override
    public String toString() {
        return "TaskWorkerConstraint{" +
                "UUID='" + UUID + '\'' +
                ", price=" + price +
                ", userUUID='" + userUUID + '\'' +
                ", taskUUID='" + taskUUID + '\'' +
                '}';
    }
}
