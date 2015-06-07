package dk.trustworks.bimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {

    @JsonProperty("uuid")
	private String UUID;
	
	private String name;

    @JsonProperty("projectuuid")
	private String projectUUID;

    private String type;

    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task() {
    }

    public Task(String UUID, String name, String projectUUID, String type) {
        this.UUID = UUID;
        this.name = name;
        this.projectUUID = projectUUID;
        this.type = type;
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

    @Override
    public String toString() {
        return "Task{" +
                "UUID='" + UUID + '\'' +
                ", name='" + name + '\'' +
                ", projectUUID='" + projectUUID + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
