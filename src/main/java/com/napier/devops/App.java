package com.napier.devops;

import java.sql.*;
import java.util.ArrayList;

/**
 * Main application class for Employee Database
 */
public class App {
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "college");  // Changed password
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Gets all the current employees and salaries.
     *
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries() {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Gets salaries for employees with a given role.
     *
     * @param title the job title to search for
     * @return A list of all employees with the given title, or null if there is an error.
     */
    public ArrayList<Employee> getSalariesByRole(String title) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, "
                            + "salaries.salary, titles.title "
                            + "FROM employees, salaries, titles "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = titles.emp_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND titles.to_date = '9999-01-01' "
                            + "AND titles.title = '" + title + "' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.title = rset.getString("titles.title");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Gets a department by name
     *
     * @param dept_name the department name to search for
     * @return Department object or null if not found
     */
    public Department getDepartment(String dept_name) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT dept_no, dept_name "
                            + "FROM departments "
                            + "WHERE dept_name = '" + dept_name + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Check one is returned
            if (rset.next()) {
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                return dept;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department details");
            return null;
        }
    }

    /**
     * Gets all employees in a department
     *
     * @param dept the department to get employees for
     * @return list of employees in the department
     */
    public ArrayList<Employee> getSalariesByDepartment(Department dept) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries, dept_emp, departments "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = dept_emp.emp_no "
                            + "AND dept_emp.dept_no = departments.dept_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND departments.dept_no = '" + dept.dept_no + "' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees.
     *
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees) {
        // Check employees is not null
        if (employees == null) {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees) {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    /**
     * Prints employee details
     *
     * @param emp Employee to display
     */
    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }

    public static void main(String[] args) {
        // Create new Application
        App app = new App();

        // Connect to database
        if (args.length < 1) {
            app.connect("localhost:33060", 30000);
        } else {
            app.connect(args[0], Integer.parseInt(args[1]));
        }

        // Test 1: Get all salaries (this will be a LOT of data - 240k+ rows)
        System.out.println("\n=== TEST 1: Getting all salaries ===");
        ArrayList<Employee> employees = app.getAllSalaries();
        if (employees != null && !employees.isEmpty()) {
            System.out.println("Found " + employees.size() + " employees");
            System.out.println("Printing first 50:");
            // Only print first 50 to avoid flooding console
            ArrayList<Employee> first50 = new ArrayList<>();
            for (int i = 0; i < Math.min(50, employees.size()); i++) {
                first50.add(employees.get(i));
            }
            app.printSalaries(first50);
        } else {
            System.out.println("No employees found!");
        }

        // Test 2: Get salaries by role (Engineer)
        System.out.println("\n=== TEST 2: Getting salaries for 'Engineer' ===");
        ArrayList<Employee> engineers = app.getSalariesByRole("Engineer");
        if (engineers != null && !engineers.isEmpty()) {
            System.out.println("Found " + engineers.size() + " engineers");
            System.out.println("Printing first 20:");
            ArrayList<Employee> first20 = new ArrayList<>();
            for (int i = 0; i < Math.min(20, engineers.size()); i++) {
                first20.add(engineers.get(i));
            }
            app.printSalaries(first20);
        } else {
            System.out.println("No engineers found!");
        }

        // Test 3: Get department and employees in that department
        System.out.println("\n=== TEST 3: Getting Sales department employees ===");
        Department dept = app.getDepartment("Sales");
        if (dept != null) {
            System.out.println("Found department: " + dept.dept_name + " (" + dept.dept_no + ")");
            ArrayList<Employee> deptEmployees = app.getSalariesByDepartment(dept);
            if (deptEmployees != null && !deptEmployees.isEmpty()) {
                System.out.println("Found " + deptEmployees.size() + " employees in Sales");
                System.out.println("Printing first 20:");
                ArrayList<Employee> first20 = new ArrayList<>();
                for (int i = 0; i < Math.min(20, deptEmployees.size()); i++) {
                    first20.add(deptEmployees.get(i));
                }
                app.printSalaries(first20);
            } else {
                System.out.println("No employees found in Sales!");
            }
        } else {
            System.out.println("Sales department not found!");
        }

        // Disconnect from database
        app.disconnect();
    }
}