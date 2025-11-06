package com.napier.devops;

public class Employee {
    public int emp_no;
    public String first_name;
    public String last_name;
    public String title;
    public int salary;
    public Department dept;
    public String dept_name;    // fallback if dept object not used
    public Employee manager;
    public String managerName;  // fallback if manager object not used
}
