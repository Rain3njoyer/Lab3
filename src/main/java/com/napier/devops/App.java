package com.napier.devops;

import java.sql.*;
import java.util.*;

/**
 * App.java - Lab 5 version (prints ALL employee salaries, with department and manager handling)
 */
public class App {
    private Connection con = null;

    // ---------------------------
    // Main entry
    // ---------------------------
    public static void main(String[] args) {
        App app = new App();
        app.connect();

        try {
            // Step 1: Get all current employee salaries
            ArrayList<Employee> employees = app.getAllSalaries();

            if (employees == null || employees.isEmpty()) {
                System.out.println("❌ No employee data found!");
            } else {
                // Step 2: Show total count
                System.out.println("✅ Number of current employees/salaries: " + employees.size());

                // Step 3: Print ALL employees (⚠️ 240k rows)
                app.printSalaries(employees);
            }
        } finally {
            app.disconnect();
        }
    }

    // ---------------------------
    // Database connection
    // ---------------------------
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 15;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database... (attempt " + (i + 1) + "/" + retries + ")");
            try {
                Thread.sleep(5000);
                String url = System.getenv().getOrDefault("JDBC_URL",
                        "jdbc:mysql://localhost:33060/employees?allowPublicKeyRetrieval=true&useSSL=false");
                String user = System.getenv().getOrDefault("DB_USER", "root");
                String pass = System.getenv().getOrDefault("DB_PASS", "example");
                con = DriverManager.getConnection(url, user, pass);
                System.out.println("✅ Successfully connected to: " + url);
                break;
            } catch (SQLException sqle) {
                System.out.println("❌ Failed to connect attempt " + (i + 1) + ": " + sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted");
            }
        }

        if (con == null) {
            System.out.println("ERROR: Could not establish a database connection.");
        }
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Disconnected from database.");
            } catch (Exception e) {
                System.out.println("Error closing connection: " + e.getMessage());
            } finally {
                con = null;
            }
        }
    }

    // ---------------------------
    // Get all current salaries
    // ---------------------------
    public ArrayList<Employee> getAllSalaries() {
        if (con == null) {
            System.out.println("No DB connection in getAllSalaries()");
            return null;
        }

        try {
            Statement stmt = con.createStatement();
            String query =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries " +
                            "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' " +
                            "ORDER BY employees.emp_no ASC";
            ResultSet rset = stmt.executeQuery(query);

            ArrayList<Employee> employees = new ArrayList<>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }

            return employees;
        } catch (Exception e) {
            System.out.println("❌ Failed to get salary details: " + e.getMessage());
            return null;
        }
    }

    // ---------------------------
    // Print ALL salaries
    // ---------------------------
    public void printSalaries(ArrayList<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employee records to print.");
            return;
        }

        System.out.printf("%-10s %-15s %-20s %-10s%n", "Emp No", "First Name", "Last Name", "Salary");
        System.out.println("---------------------------------------------------------------");

        for (Employee emp : employees) {
            System.out.printf("%-10d %-15s %-20s %-10d%n",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary);
        }

        System.out.println("✅ Printed all " + employees.size() + " employee salary rows.");
    }

    // ---------------------------
    // Display single Employee (safe version)
    // ---------------------------
    public void displayEmployee(Employee emp) {
        if (emp == null) {
            System.out.println("Employee not found");
            return;
        }

        System.out.println("\nEmployee Details:");
        System.out.println("Emp No: " + emp.emp_no);
        System.out.println("First Name: " + emp.first_name);
        System.out.println("Last Name: " + emp.last_name);
        System.out.println("Title: " + (emp.title != null ? emp.title : "(unknown)"));
        System.out.println("Salary: " + emp.salary);

        // Department name
        String deptName = null;
        if (emp.dept != null && emp.dept.dept_name != null)
            deptName = emp.dept.dept_name;
        else if (emp.dept_name != null)
            deptName = emp.dept_name;
        System.out.println("Department: " + (deptName != null ? deptName : "(unknown)"));

        // Manager
        String managerName = null;
        if (emp.manager != null) {
            managerName = emp.manager.first_name + " " + emp.manager.last_name;
        } else if (emp.managerName != null) {
            managerName = emp.managerName;
        }
        System.out.println("Manager: " + (managerName != null ? managerName : "(unknown)"));
    }
}
