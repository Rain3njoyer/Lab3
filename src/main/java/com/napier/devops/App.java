package com.napier.devops;

import java.sql.*;
import java.util.*;

/**
 * App.java - Combined Lab 3 / Lab 4 / Lab 5 version.
 *
 * Default behaviour: prints ALL matching rows (no 50-row limit).
 *
 * WARNING: Printing everything can be very large (~240k rows). Prefer redirecting output to a file.
 */
public class App {
    private Connection con = null;

    // ---------------------------
    // MAIN - prints ALL results
    // ---------------------------
    public static void main(String[] args) {
        App app = new App();
        app.connect();

        try {
            // By default: print ALL current salaries (Lab4)
            // If you want instead to run department-based printing, set mode to "dept" and pass department name as second arg.
            // Examples:
            //  - java ... App all
            //  - java ... App dept "Sales"
            String mode = (args.length > 0) ? args[0].toLowerCase() : "all";

            if ("dept".equals(mode)) {
                String deptName = (args.length > 1) ? args[1] : "Sales";
                Department dept = app.getDepartment(deptName);
                if (dept == null) {
                    System.out.println("Department not found: " + deptName);
                } else {
                    ArrayList<Employee> emps = app.getSalariesByDepartment(dept);
                    System.out.println("Total employees in department " + deptName + ": " + emps.size());
                    app.printSalaries(emps); // prints ALL for department
                }
            } else if ("role".equals(mode)) {
                String role = (args.length > 1) ? args[1] : "Engineer";
                List<SalaryRow> rows = app.getSalariesByRole(role);
                System.out.println("Total rows for role " + role + ": " + rows.size());
                app.printSalariesByRoleAll(rows);
            } else {
                // default all salaries
                ArrayList<Employee> employees = app.getAllSalaries();
                if (employees == null || employees.isEmpty()) {
                    System.out.println("No employee data found!");
                } else {
                    System.out.println("Number of current employees/salaries: " + employees.size());
                    app.printSalaries(employees); // prints ALL employees
                }
            }
        } finally {
            app.disconnect();
        }
    }

    // ---------------------------
    // Database connection helpers
    // ---------------------------
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
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
    // Lab 3 - basic getEmployee
    // ---------------------------
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
            System.out.println("Failed to get employee details: " + e.getMessage());
            return null;
        }
    }

    // ---------------------------
    // Lab 4 - getAllSalaries
    // ---------------------------
    public ArrayList<Employee> getAllSalaries() {
        if (con == null) {
            System.out.println("No DB connection in getAllSalaries()");
            return null;
        }

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
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
    // Lab 3b - salaries by role
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

    public void printSalariesByRoleAll(List<SalaryRow> rows) {
        System.out.printf("%-8s %-15s %-15s %-8s%n","emp_no","first_name","last_name","salary");
        for (SalaryRow r : rows) {
            System.out.printf("%-8d %-15s %-15s %-8d%n", r.emp_no, r.first_name, r.last_name, r.salary);
        }
        System.out.println("✅ Printed all rows for role. Count: " + rows.size());
    }

    // ---------------------------
    // Lab 5 - Departments
    // ---------------------------
    public Department getDepartment(String dept_name) {
        if (con == null) {
            System.out.println("No DB connection in getDepartment()");
            return null;
        }

        String sql = "SELECT d.dept_no, d.dept_name, dm.emp_no as mgr_emp_no " +
                "FROM departments d " +
                "LEFT JOIN dept_manager dm ON d.dept_no = dm.dept_no " +
                "WHERE d.dept_name = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dept_name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department dept = new Department();
                    dept.dept_no = rs.getString("dept_no");
                    dept.dept_name = rs.getString("dept_name");
                    int mgrEmpNo = rs.getInt("mgr_emp_no");
                    if (!rs.wasNull()) {
                        Employee mgr = getEmployee(mgrEmpNo);
                        dept.manager = mgr;
                    } else {
                        dept.manager = null;
                    }
                    return dept;
                } else {
                    System.out.println("No department found for name: " + dept_name);
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getDepartment: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Employee> getSalariesByDepartment(Department dept) {
        ArrayList<Employee> list = new ArrayList<>();
        if (con == null) {
            System.out.println("No DB connection in getSalariesByDepartment()");
            return list;
        }
        if (dept == null || dept.dept_no == null) {
            System.out.println("Invalid department supplied");
            return list;
        }

        String sql = "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                "FROM employees e " +
                "JOIN salaries s ON e.emp_no = s.emp_no " +
                "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                "WHERE s.to_date = '9999-01-01' " +
                "AND de.dept_no = ? " +
                "ORDER BY e.emp_no ASC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dept.dept_no);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee();
                    emp.emp_no = rs.getInt("emp_no");
                    emp.first_name = rs.getString("first_name");
                    emp.last_name = rs.getString("last_name");
                    emp.salary = rs.getInt("salary");
                    emp.dept = dept;
                    emp.manager = dept.manager;
                    list.add(emp);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error in getSalariesByDepartment: " + e.getMessage());
        }
        return list;
    }

    // ---------------------------
    // Printing helpers - prints ALL rows
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
}
