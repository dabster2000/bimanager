package dk.trustworks.bimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("uuid")
	private String UUID;

    @JsonProperty("customerreference")
	private String customerReference;
	
	private String name;
	
	private double budget;

    @JsonProperty("clientuuid")
	private String clientUUID;

    @JsonProperty("clientdatauuid")
	private String clientDataUUID;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonProperty("enddate")
    private LocalDate endDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonProperty("startdate")
    private LocalDate startDate;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("userowneruuid")
    private String userOwnerUUID;
	
	private boolean active;

    @JsonProperty("tasks")
    private List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    public Project(String UUID, String customerReference, String name, double budget, String clientUUID, String clientDataUUID, LocalDate endDate, LocalDate startDate, Date created, boolean active) {
        this.UUID = UUID;
        this.customerReference = customerReference;
        this.name = name;
        this.budget = budget;
        this.clientUUID = clientUUID;
        this.clientDataUUID = clientDataUUID;
        this.endDate = endDate;
        this.startDate = startDate;
        this.created = created;
        this.active = active;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getClientUUID() {
        return clientUUID;
    }

    public void setClientUUID(String clientUUID) {
        this.clientUUID = clientUUID;
    }

    public String getClientDataUUID() {
        return clientDataUUID;
    }

    public void setClientDataUUID(String clientDataUUID) {
        this.clientDataUUID = clientDataUUID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getUserOwnerUUID() {
        return userOwnerUUID;
    }

    public void setUserOwnerUUID(String userOwnerUUID) {
        this.userOwnerUUID = userOwnerUUID;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Project{" +
                "UUID='" + UUID + '\'' +
                ", customerReference='" + customerReference + '\'' +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", clientUUID='" + clientUUID + '\'' +
                ", clientDataUUID='" + clientDataUUID + '\'' +
                ", endDate=" + endDate +
                ", startDate=" + startDate +
                ", created=" + created +
                ", userOwnerUUID='" + userOwnerUUID + '\'' +
                ", active=" + active +
                '}';
    }

}
