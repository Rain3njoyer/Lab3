package com.napier.devops;

public class Employee {
    public int emp_no;
    public String first_name;
    public String last_name;
    public String title;      // optional
    public int salary;
    public Department dept;   // assigned in getSalariesByDepartment
    public Employee manager;  // optional
}
