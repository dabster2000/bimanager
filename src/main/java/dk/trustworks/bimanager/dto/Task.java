package dk.trustworks.bimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("uuid")
	private String UUID;
	
	private String name;

    @JsonProperty("projectuuid")
	private String projectUUID;

    private String type;

    private Project project;

    @JsonProperty("taskworkerconstraints")
    private List<TaskWorkerConstraint> taskWorkerConstraints = new ArrayList<>();

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task() {
    }

    public Task(String name, Project project, String projectUUID, List<TaskWorkerConstraint> taskWorkerConstraints, String type, String UUID) {
        this.name = name;
        this.project = project;
        this.projectUUID = projectUUID;
        this.taskWorkerConstraints = taskWorkerConstraints;
        this.type = type;
        this.UUID = UUID;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectUUID() {
        return projectUUID;
    }

    public void setProjectUUID(String projectUUID) {
        this.projectUUID = projectUUID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TaskWorkerConstraint> getTaskWorkerConstraints() {
        return taskWorkerConstraints;
    }

    public void setTaskWorkerConstraints(List<TaskWorkerConstraint> taskWorkerConstraints) {
        this.taskWorkerConstraints = taskWorkerConstraints;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", UUID='" + UUID + '\'' +
                ", projectUUID='" + projectUUID + '\'' +
                ", type='" + type + '\'' +
                ", project=" + project +
                ", taskWorkerConstraints=" + taskWorkerConstraints +
                '}';
    }
}
