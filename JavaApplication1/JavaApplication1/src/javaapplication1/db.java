/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class db {

    private static final String URL = "jdbc:postgresql://localhost:5432/users"; // Database URL
    private static final String USER = "test"; // Database username
    private static final String PASSWORD = "test"; // Database password

    // Global variables to store employee data
    private double basicSalary;
    private double riceAllowance = 1500; // Default value for rice allowance
    private double phoneAllowance = 1000; // Default value for phone allowance
    private double clothingAllowance = 1500; // Default value for clothing allowance

    public static void main(String args[]) {
        db database = new db();
        database.loadEmployeeData(1); // Example: Load data for employee with ID 1
        double netSalary = database.calculateSalary(1500, 1000, 1500, 0.05, 0.05, 0.12);
        System.out.println("Net Salary: " + netSalary);
    }

    // Establishes a connection to the PostgreSQL database
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver"); // Load the PostgreSQL driver class (optional since JDBC 4.0)
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Include it in your library path.");
            return null;
        }
        return DriverManager.getConnection(URL, USER, PASSWORD); // Return a connection to the database
    }

    // Authenticates a user with the given username and password
    public boolean authenticate(String username, String password) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM public.users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Return true if a match is found
        } catch (SQLException e) {
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return false;
        }
    }

    // Registers a new user with the given details
    public boolean register(String firstname, String lastname, String username, String password) {
        String sql = "INSERT INTO public.users (firstname, lastname, username, password) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, firstname);
            pstmt.setString(2, lastname);
            pstmt.setString(3, username);
            pstmt.setString(4, password);
            int rowsAffected = pstmt.executeUpdate(); // Execute the update
            return rowsAffected > 0; // Return true if the insertion was successful
        } catch (SQLException e) {
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return false;
        }
    }

    // Retrieves all employees from the database and returns them as a list
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM users.employees ORDER BY id ASC";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getInt("id"));
                employee.setFirst_name(resultSet.getString("first_name"));
                employee.setLast_name(resultSet.getString("last_name"));
                employee.setBirthday(resultSet.getDate("birthday"));
                employee.setAddress(resultSet.getString("address"));
                employee.setPhone(resultSet.getString("phone"));
                employee.setSss(resultSet.getString("sss"));
                employee.setTin(resultSet.getString("tin"));
                employee.setPagibig(resultSet.getString("pagibig"));
                employee.setStatus(resultSet.getString("status"));
                employee.setDesignation(resultSet.getString("designation"));
                employee.setSupervisor(resultSet.getString("supervisor"));
                employee.setPhilhealth(resultSet.getString("philhealth"));
                employee.setBasic_salary(resultSet.getDouble("basic_salary"));
                employee.setRice_subsidy(resultSet.getDouble("rice_subsidy"));
                employee.setPhone_allowance(resultSet.getDouble("phone_allowance"));
                employee.setClothing_allowance(resultSet.getDouble("clothing_allowance"));
                employee.setGross_semi_monthly_rate(resultSet.getDouble("gross_semi_monthly_rate"));
                employee.setHourly_rate(resultSet.getDouble("hourly_rate"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            // Log the exception or handle it as needed
        }
        return employees;
    }

    // Updates an employee's record in the database
    public boolean updateRecord(String lastName, String firstName, String sss, String philhealth, String tin, String pagibig, String employeeNo) {
        String query = "UPDATE users.employees SET first_name = ?, last_name = ?, sss = ?, philhealth = ?, tin = ?, pagibig = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, sss);
            statement.setString(4, philhealth);
            statement.setString(5, tin);
            statement.setString(6, pagibig);
            statement.setInt(7, Integer.parseInt(employeeNo)); // Assuming lblEmployeeNo is the employee ID label
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            return false;
        }
    }

    // Deletes an employee's record from the database
    public boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM users.employees WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if the deletion was successful
        } catch (SQLException e) {
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return false;
        }
    }

    // Loads employee data from the database and stores it in global variables
    public void loadEmployeeData(int employeeId) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users.employees WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.basicSalary = resultSet.getDouble("basic_salary");
                // Load other fields as needed
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee data: " + e.getMessage());
        }
    }

    // Calculates the net salary based on allowances and deduction rates
    public double calculateSalary(double riceAllowance, double phoneAllowance, double clothingAllowance,
                                  double philhealthRate, double pagibigRate, double sssRate) {
        // Calculate deductions based on the basic salary and provided rates
        double philhealthDeduction = this.basicSalary * philhealthRate;
        double pagibigDeduction = this.basicSalary * pagibigRate;
        double sssDeduction = this.basicSalary * sssRate;

        // Calculate gross salary
        double grossSalary = this.basicSalary + riceAllowance + phoneAllowance + clothingAllowance;

        // Calculate total deductions
        double totalDeductions = philhealthDeduction + pagibigDeduction + sssDeduction;

        // Calculate net salary
        return grossSalary - totalDeductions;
    }
}

