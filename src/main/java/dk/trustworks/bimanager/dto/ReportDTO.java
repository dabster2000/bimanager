package dk.trustworks.bimanager.dto;

public class ReportDTO {

    private String clientName;

    private String projectName;

    private String taskUUID;

    private String taskName;

    private String workerUUID;

    private String workerName;

    private double hours;

    private double rate;

    private double sum;

    public ReportDTO() {
    }

    public ReportDTO(String clientName, String projectName, String taskUUID, String taskName, String workerUUID, String workerName, double hours, double rate, double sum) {
        this.clientName = clientName;
        this.projectName = projectName;
        this.taskUUID = taskUUID;
        this.taskName = taskName;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.hours = hours;
        this.rate = rate;
        this.sum = sum;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getWorkerUUID() {
        return workerUUID;
    }

    public void setWorkerUUID(String workerUUID) {
        this.workerUUID = workerUUID;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
