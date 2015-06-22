package dk.trustworks.bimanager.dto;

import java.util.Arrays;

public class ProjectYearEconomy {

	private String projectUUID;
	private String projectName;
	private double[] amount = new double[12];
    private double[] actual = new double[12];

    public double[] getActual() {
        return actual;
    }

    public void setActual(double[] actual) {
        this.actual = actual;
    }

	public ProjectYearEconomy() {
	}

	public ProjectYearEconomy(String projectUUID, String projectName) {
		this.projectName = projectName;
		this.projectUUID = projectUUID;
	}

	public ProjectYearEconomy(double[] amount, String projectName, String projectUUID) {
		this.amount = amount;
		this.projectName = projectName;
		this.projectUUID = projectUUID;
	}

    public double[] getAmount() {
        return amount;
    }

    public void setAmount(double[] amount) {
        this.amount = amount;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectUUID() {
        return projectUUID;
    }

    public void setProjectUUID(String projectUUID) {
        this.projectUUID = projectUUID;
    }

    @Override
    public String toString() {
        return "ProjectYearEconomy{" +
                "actual=" + Arrays.toString(actual) +
                ", projectUUID='" + projectUUID + '\'' +
                ", projectName='" + projectName + '\'' +
                ", amount=" + Arrays.toString(amount) +
                '}';
    }
}
