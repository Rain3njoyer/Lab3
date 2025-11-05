package com.napier.devops;

import java.sql.*;
import java.util.*; // List, ArrayList, etc.

public class App {
    private Connection con = null;

    public static void main(String[] args) {
        App app = new App();
        app.connect();

        String title = System.getenv().getOrDefault("ROLE_TITLE", (args.length > 0 ? args[0] : "Engineer"));
        System.out.println("Getting salaries for role: " + title);
        app.printSalariesByRole(title);

        app.disconnect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 15; // increased retries to give DB more time on slow machines
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database... (attempt " + (i+1) + "/" + retries + ")");
            try {
                Thread.sleep(5000); // wait a bit for DB to start
                // Default to host-mapped port for running in IntelliJ (localhost:33060).
                // If running inside Docker compose, set JDBC_URL env var to jdbc:mysql://db:3306/employees...
                String url = System.getenv().getOrDefault("JDBC_URL", "jdbc:mysql://localhost:33060/employees?allowPublicKeyRetrieval=true&useSSL=false");
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
                // later queries can fill title, salary, dept_name, manager
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

    // Inner DTO for salary rows
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
}
