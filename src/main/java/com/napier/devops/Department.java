package com.napier.devops;

/**
 * Represents a Department.
 */
public class Department {
    public String dept_no;    // e.g. "d001"
    public String dept_name;  // e.g. "Sales"
    public Employee manager;  // manager Employee (may be null)

    public Department() {}
}
