package dk.trustworks.bimanager.dto;

public class ProjectYearEconomy {

	private String projectUUID;
	private String projectName;
	private double[] amount = new double[12];

	public ProjectYearEconomy() {
	}

	public ProjectYearEconomy(String projectName, String projectUUID) {
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
}
