package com.napier.devops;

import java.sql.*;
import java.util.*;

/**
 * App.java - combined version for Lab 3 / 3b / 4.
 *
 * Usage examples:
 *  - Run all salaries count:      java com.napier.devops.App all
 *  - Run all salaries and print:  java com.napier.devops.App all print
 *  - Run role salaries:           java com.napier.devops.App role "Engineer"
 *  - Default behavior:            java com.napier.devops.App
 *
 * Note:
 *  - If running inside Docker Compose, set JDBC_URL to jdbc:mysql://db:3306/employees...
 *  - If running on host (IntelliJ), set JDBC_URL to jdbc:mysql://localhost:33060/employees...
 */
public class App
{
    private Connection con = null;

    // ---------------------------
    // Main entry
    // ---------------------------
    public static void main(String[] args)
    {
        App app = new App();
        app.connect();

        // simple CLI mode handling
        String mode = (args.length > 0) ? args[0].toLowerCase() : "role";

        try {
            if ("all".equals(mode)) {
                // Get all salaries
                ArrayList<Employee> employees = app.getAllSalaries();
                if (employees != null) {
                    System.out.println("Number of current employees/salaries: " + employees.size());
                    // Optionally print first N entries if second arg is "print" or "printN"
                    if (args.length > 1 && args[1].toLowerCase().startsWith("print")) {
                        int n = 50; // default print count
                        if (args.length > 1) {
                            // allow "print" or "printN" where N e.g. print10
                            try {
                                if (args[1].length() > 5) {
                                    n = Integer.parseInt(args[1].substring(5));
                                } else if (args.length > 2) {
                                    n = Integer.parseInt(args[2]);
                                }
                            } catch (Exception e) {
                                // ignore parse errors, keep default
                            }
                        }
                        app.printSalariesLimited(employees, n);
                    }
                } else {
                    System.out.println("employees was null");
                }
            } else { // default role mode
                String role = System.getenv().getOrDefault("ROLE_TITLE", (args.length > 1 ? args[1] : "Engineer"));
                System.out.println("Getting salaries for role: " + role);
                app.printSalariesByRole(role);
            }
        } finally {
            app.disconnect();
        }
    }

    // ---------------------------
    // Connection management
    // ---------------------------
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 15; // give DB time to start
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database... (attempt " + (i+1) + "/" + retries + ")");
            try {
                Thread.sleep(5000); // wait a bit for DB to start
                // Default to host-mapped port for running in IntelliJ:
                String url = System.getenv().getOrDefault("JDBC_URL",
                        "jdbc:mysql://localhost:33060/employees?allowPublicKeyRetrieval=true&useSSL=false");
                String user = System.getenv().getOrDefault("DB_USER", "root");
                String pass = System.getenv().getOrDefault("DB_PASS", "example");
                con = DriverManager.getConnection(url, user, pass);
                System.out.println("Successfully connected to: " + url);
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + (i+1) + ": " + sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted");
            }
        }

        if (con == null) {
            System.out.println("ERROR: Could not establish a database connection after retries.");
        }
    }

    public void disconnect() {
        if (con != null) {
            try { con.close(); } catch (Exception e) { System.out.println("Error closing connection: " + e.getMessage()); }
            finally { con = null; }
        }
    }

    // ---------------------------
    // Existing Employee utilities
    // ---------------------------
    /**
     * Returns a minimal Employee by ID (existing from Lab 3a).
     * Make sure you have Employee.java in the same package.
     */
    public Employee getEmployee(int ID) {
        if (con == null) {
            System.out.println("No DB connection available in getEmployee()");
            return null;
        }
        try {
            Statement stmt = con.createStatement();
            String strSelect = "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = " + ID;
            ResultSet rset = stmt.executeQuery(strSelect);
            if (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                return emp;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(emp.emp_no + " " + emp.first_name + " " + emp.last_name);
            System.out.println(emp.title);
            System.out.println("Salary:" + emp.salary);
            System.out.println(emp.dept_name);
            System.out.println("Manager: " + emp.manager);
        } else {
            System.out.println("Employee not found");
        }
    }

    // ---------------------------
    // Salaries by role (Lab 3b)
    // ---------------------------
    public static class SalaryRow {
        public int emp_no;
        public String first_name;
        public String last_name;
        public int salary;
    }

    public List<SalaryRow> getSalariesByRole(String title) {
        List<SalaryRow> rows = new ArrayList<>();
        if (con == null) {
            System.out.println("No DB connection");
            return rows;
        }
        String sql = "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                + "FROM employees, salaries, titles "
                + "WHERE employees.emp_no = salaries.emp_no "
                + "AND employees.emp_no = titles.emp_no "
                + "AND salaries.to_date = '9999-01-01' "
                + "AND titles.to_date = '9999-01-01' "
                + "AND titles.title = ? "
                + "ORDER BY employees.emp_no ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SalaryRow r = new SalaryRow();
                    r.emp_no = rs.getInt("emp_no");
                    r.first_name = rs.getString("first_name");
                    r.last_name = rs.getString("last_name");
                    r.salary = rs.getInt("salary");
                    rows.add(r);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error in getSalariesByRole: " + e.getMessage());
        }
        return rows;
    }

    public void printSalariesByRole(String title) {
        List<SalaryRow> rows = getSalariesByRole(title);
        if (rows.isEmpty()) {
            System.out.println("No results for title: " + title);
            return;
        }
        System.out.printf("%-8s %-15s %-15s %-8s%n","emp_no","first_name","last_name","salary");
        for (SalaryRow r : rows) {
            System.out.printf("%-8d %-15s %-15s %-8d%n", r.emp_no, r.first_name, r.last_name, r.salary);
        }
    }

    // ---------------------------
    // Get all current salaries (Lab 4)
    // ---------------------------
    /**
     * Returns all employees with current salaries (salaries.to_date = '9999-01-01').
     * Uses Employee DTO (ensure Employee has a public int salary field).
     */
    public ArrayList<Employee> getAllSalaries()
    {
        if (con == null) {
            System.out.println("No DB connection in getAllSalaries()");
            return null;
        }
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            ResultSet rset = stmt.executeQuery(strSelect);

            ArrayList<Employee> employees = new ArrayList<>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary"); // ensure Employee has this field
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints full list in a table format. Careful: this can be very long.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        if (employees == null) {
            System.out.println("No employees to print (null).");
            return;
        }
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        for (Employee emp : employees)
        {
            System.out.println(String.format("%-10d %-15s %-20s %-8d",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary));
        }
    }

    /**
     * Print first N rows safely.
     */
    public void printSalariesLimited(ArrayList<Employee> employees, int n) {
        if (employees == null) {
            System.out.println("No employees to print.");
            return;
        }
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        int limit = Math.min(n, employees.size());
        for (int i = 0; i < limit; i++) {
            Employee emp = employees.get(i);
            System.out.println(String.format("%-10d %-15s %-20s %-8d",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary));
        }
        if (employees.size() > limit) {
            System.out.println("... printed " + limit + " of " + employees.size() + " rows.");
        }
    }
}
