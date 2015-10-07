package dk.trustworks.bimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Budget {

    private String name;

    @JsonProperty("remainingbudget")
    private double remainingBudget;

    @JsonProperty("usedbudget")
    private double usedBudget;

    private double budget;

    public Budget() {
    }

    public Budget(String name, double remainingBudget, double usedBudget, double budget) {
        this.name = name;
        this.remainingBudget = remainingBudget;
        this.usedBudget = usedBudget;
        this.budget = budget;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public double getUsedBudget() {
        return usedBudget;
    }

    public void setUsedBudget(double usedBudget) {
        this.usedBudget = usedBudget;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
