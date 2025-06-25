/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for direct Employee table database interactions.
 *
 * This test class verifies the basic CRUD (Create, Read, Update, Delete)
 * functionality against the 'employee' table in the database. It ensures that
 * the database connection is working and that fundamental SQL operations are successful.
 *
 * @author paulomolina
 */
public class EmployeeRepositoryTest {

    private static Connection connection;

    @BeforeAll
    public static void setup() throws SQLException {
        // This is a great place for an initial display message
        System.out.println("--- Starting EmployeeTest: Setting up database connection. ---");
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Database connection established successfully.");
        System.out.println("----------------------------------------------------------");
    }

    @Test
    public void testEmployeeExists_Success() throws SQLException {
        // Display what this test is doing
        System.out.println("Running test: testEmployeeExists_Success");
        System.out.println("Checking for employee with ID: 10002...");

        String query = "SELECT * FROM public.employee WHERE employee_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, 10002);

        ResultSet rs = stmt.executeQuery();

        // The assertion itself
        assertTrue(rs.next(), "employee with ID 10002 should exist");
        assertEquals("10002", rs.getString("employee_id"), "Retrieved employee ID should match");

        // Display the result of the test
        System.out.println("Result: Employee found. Test PASSED.\n");
    }

    @Test
    public void testEmployeeDoesNotExist_Failure() throws SQLException {
        System.out.println("Running test: testEmployeeDoesNotExist_Failure");
        int nonExistentEmployeeId = 99999;
        System.out.println("Checking for non-existent employee with ID: " + nonExistentEmployeeId + "...");

        String query = "SELECT * FROM public.employee WHERE employee_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, nonExistentEmployeeId);

        ResultSet rs = stmt.executeQuery();

        assertFalse(rs.next(), "Query for non-existent employee ID should return no results.");

        System.out.println("Result: Employee correctly not found. Test PASSED.\n");
    }

    @Test
    public void testEmployeeFirstNameIsCorrect() throws SQLException {
        System.out.println("Running test: testEmployeeFirstNameIsCorrect");
        int employeeIdToCheck = 10004;
        String expectedFirstName = "Anthony";
        System.out.println("Verifying first name for employee ID: " + employeeIdToCheck + "...");

        String query = "SELECT firstname FROM public.employee WHERE employee_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, employeeIdToCheck);

        ResultSet rs = stmt.executeQuery();

        assertTrue(rs.next(), "Employee with ID 10004 should exist to check their first name.");

        String actualFirstName = rs.getString("firstname");
        System.out.println("Expected first name: '" + expectedFirstName + "', Actual first name: '" + actualFirstName + "'");
        assertEquals(expectedFirstName, actualFirstName, "The first name for employee 10004 should be 'Anthony'.");
        
        System.out.println("Result: First name matches. Test PASSED.\n");
    }

    @Test
    public void testInsertAndRetrieveNewEmployee() throws SQLException {
        System.out.println("Running test: testInsertAndRetrieveNewEmployee");
        int newEmployeeId = 55555;
        String newFirstName = "Test";
        String newLastName = "User";
        String sss = "test-sss";
        
        System.out.println("Step 1: Inserting new employee with ID: " + newEmployeeId + "...");
        String insertQuery = "INSERT INTO public.employee(employee_id, firstname, lastname, sss) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, newEmployeeId);
            insertStmt.setString(2, newFirstName);
            insertStmt.setString(3, newLastName);
            insertStmt.setString(4, sss);
            int rowsAffected = insertStmt.executeUpdate();
            assertEquals(1, rowsAffected, "One row should have been inserted.");
            System.out.println("...Insert successful.");
        }

        System.out.println("Step 2: Retrieving the newly inserted employee...");
        String selectQuery = "SELECT * FROM public.employee WHERE employee_id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, newEmployeeId);
            ResultSet rs = selectStmt.executeQuery();
            assertTrue(rs.next(), "The newly inserted employee should be found.");
            assertEquals(newLastName, rs.getString("lastname"), "The last name of the retrieved employee should match.");
            System.out.println("...Retrieve successful.");
        }

        System.out.println("Step 3: Cleaning up by deleting the test employee...");
        String deleteQuery = "DELETE FROM public.employee WHERE employee_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, newEmployeeId);
            deleteStmt.executeUpdate();
            System.out.println("...Cleanup successful.");
        }
        
        System.out.println("Result: Full Insert-Retrieve-Delete cycle successful. Test PASSED.\n");
    }

    @AfterAll
    public static void teardown() throws SQLException {
        System.out.println("----------------------------------------------------------");
        System.out.println("--- Finished EmployeeTest: Closing database connection. ---");
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        System.out.println("Database connection closed.");
    }
}
