package com.mindex.challenge.data;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    public ReportingStructure(Employee employee, int number) {
        this.employee = employee;
        numberOfReports = number;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getDirectReports() {
        return numberOfReports;
    }

    public void setDirectReports(int directReports) {
        this.numberOfReports = directReports;
    }
}
