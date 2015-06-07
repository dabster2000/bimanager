package dk.trustworks.bimanager.dto;

import java.util.UUID;

public class MonthBudget {

    private UUID UUID;

    private int year;

    private int month;

    private double budget;
    
    private UUID projectUUID;

    public MonthBudget() {
    }

    public MonthBudget(java.util.UUID UUID, int year, int month, double budget, java.util.UUID projectUUID) {
        this.UUID = UUID;
        this.year = year;
        this.month = month;
        this.budget = budget;
        this.projectUUID = projectUUID;
    }

    public UUID getUUID() {
        return UUID;
    }

    public void setUUID(UUID UUID) {
        this.UUID = UUID;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public UUID getProjectUUID() {
        return projectUUID;
    }

    public void setProjectUUID(UUID projectUUID) {
        this.projectUUID = projectUUID;
    }
}
