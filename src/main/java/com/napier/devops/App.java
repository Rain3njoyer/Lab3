package com.napier.devops;

import java.sql.*;

public class App {
    private Connection con = null;

    public static void main(String[] args) {
        App app = new App();
        app.connect();
        Employee emp = app.getEmployee(255530); // example ID; change as needed
        app.displayEmployee(emp);
        app.disconnect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                Thread.sleep(5000); // wait a bit for DB to start
                // default to host-mapped port for running in IntelliJ (localhost:33060)
                String url = System.getenv().getOrDefault("JDBC_URL", "jdbc:mysql://localhost:33060/employees?allowPublicKeyRetrieval=true&useSSL=false");
                String user = System.getenv().getOrDefault("DB_USER", "root");
                String pass = System.getenv().getOrDefault("DB_PASS", "example");
                con = DriverManager.getConnection(url, user, pass);
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted");
            }
        }
    }

    public void disconnect() {
        if (con != null) {
            try { con.close(); } catch (Exception e) { System.out.println("Error closing connection"); }
        }
    }

    public Employee getEmployee(int ID) {
        try {
            Statement stmt = con.createStatement();
            String strSelect = "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = " + ID;
            ResultSet rset = stmt.executeQuery(strSelect);
            if (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                // later queries will fill title, salary, dept_name, manager
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
}
