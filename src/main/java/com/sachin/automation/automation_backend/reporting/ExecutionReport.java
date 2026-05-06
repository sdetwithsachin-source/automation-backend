package com.sachin.automation.automation_backend.reporting;

import java.util.ArrayList;
import java.util.List;

public class ExecutionReport {

    private String testName;
    private String status;
    private double totalExecutionTime;

    private List<StepResult> steps = new ArrayList<>();

    private List<String> consoleLogs = new ArrayList<>();

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public void setTotalExecutionTime(double totalExecutionTime) {
        this.totalExecutionTime = totalExecutionTime;
    }

    public List<StepResult> getSteps() {
        return steps;
    }

    public void setSteps(List<StepResult> steps) {
        this.steps = steps;
    }

    public List<String> getConsoleLogs() {
        return consoleLogs;
    }

    public void setConsoleLogs(List<String> consoleLogs) {
        this.consoleLogs = consoleLogs;
    }
}
