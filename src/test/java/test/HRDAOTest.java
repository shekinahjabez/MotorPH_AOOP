/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Employee;
import com.payroll.domain.Person;
import com.payroll.DAO.HRDAO;
import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Unit Test for the HRService class.
 *
 * This test class verifies the core CRUD (Create, Read, Update, Delete)
 * operations for employee data management.
 *
 * @author paulomolina
 */
public class HRDAOTest {

    private static Connection connection;
    private static HRDAO hrService;

    // A temporary ID for our test employee that is unlikely to exist.
    private static int testEmployeeId;

    @BeforeAll
    public static void setup() throws SQLException {
        System.out.println("--- Starting HRServiceTest: Setting up database connection and service. ---");
        String url = "jdbc:postgresql://localhost:5432/motorph_db";
        String user = "postgres";
        String password = "postgres";
        connection = DriverManager.getConnection(url, user, password);
        hrService = new HRDAO(connection);
        System.out.println("Database connection and service initialized.");
        System.out.println("---------------------------------------------------------------------------");
    }

    // This method will run before each test to ensure no leftover data interferes.
    @BeforeEach
    public void cleanupBeforeTest() {
        // Attempt to delete the employee if they were left over from a failed test run.
        // We check if testEmployeeId has been set yet.
        if (testEmployeeId > 0) {
            hrService.deleteEmployeeDetails(testEmployeeId);
        }
    }

    @Test
    public void testEmployee_CRUD_Cycle() {
        System.out.println("Running test: testEmployee_CRUD_Cycle");

        // --- 1. CREATE (using addEmployeeDetails) ---
        System.out.println("Step 1: Creating a new employee record...");
        Person newEmployee = new Employee();
        newEmployee.setFirstName("Test");
        newEmployee.setLastName("User");
        newEmployee.setEmpAddress("123 Test Lane");
        newEmployee.setEmpBirthday(new Date());
        newEmployee.setEmpPhoneNumber("555-0123");
        newEmployee.setEmpSSS("SSS-TEST");
        newEmployee.setEmpTIN("TIN-TEST");
        newEmployee.setEmpPagibig(12345L);
        newEmployee.setEmpPhilHealth(54321L);
        // Set Position and Status - assuming IDs 1 and 1 exist in your DB.
        EmployeePosition pos = new EmployeePosition(); pos.setId(1);
        EmployeeStatus stat = new EmployeeStatus(); stat.setId(1);
        newEmployee.setEmpPosition(pos);
        newEmployee.setEmpStatus(stat);

        Person createdEmployee = hrService.addEmployeeDetails(newEmployee);
        testEmployeeId = createdEmployee.getEmpID(); // Save the generated ID for later steps
        
        assertTrue(testEmployeeId > 0, "addEmployeeDetails should return an employee with a generated ID.");
        assertEquals("Test", createdEmployee.getFirstName());
        System.out.println("...Create operation successful. New Employee ID: " + testEmployeeId);

        // --- 2. READ (using getByEmpID) ---
        System.out.println("Step 2: Reading the created employee record...");
        Person retrievedEmployee = hrService.getByEmpID(testEmployeeId);
        assertNotNull(retrievedEmployee, "getByEmpID should find the newly created employee.");
        assertEquals("User", retrievedEmployee.getLastName(), "Last name of retrieved employee is incorrect.");
        System.out.println("...Read operation verified successfully.");

        // --- 3. UPDATE (using updateEmployeeDetails) ---
        System.out.println("Step 3: Updating the employee's address...");
        String newAddress = "456 Updated Ave.";
        retrievedEmployee.setEmpAddress(newAddress);
        hrService.updateEmployeeDetails(retrievedEmployee);

        Person updatedEmployee = hrService.getByEmpID(testEmployeeId);
        assertEquals(newAddress, updatedEmployee.getEmpAddress(), "Employee address should have been updated.");
        System.out.println("...Update operation verified successfully.");

        // --- 4. DELETE (using deleteEmployeeDetails) ---
        System.out.println("Step 4: Deleting the test employee record...");
        boolean isDeleted = hrService.deleteEmployeeDetails(testEmployeeId);
        assertTrue(isDeleted, "deleteEmployeeDetails should return true for a successful deletion.");

        // Verify deletion by trying to read again
        Person deletedEmployee = hrService.getByEmpID(testEmployeeId);
        assertNull(deletedEmployee, "Employee should be null after deletion.");
        System.out.println("...Delete operation verified successfully.");

        System.out.println("Result: Employee CRUD cycle completed successfully. Test PASSED.\n");
    }

    @Test
    public void testGetAllEmployee_ReturnsNonEmptyList() {
        System.out.println("Running test: testGetAllEmployee_ReturnsNonEmptyList");
        System.out.println("Verifying that getAllEmployee returns a list of employees...");

        // Act
        List<Person> allEmployees = hrService.getAllEmployee();

        // Assert
        assertNotNull(allEmployees, "The returned list should not be null.");
        assertFalse(allEmployees.isEmpty(), "The employee list should not be empty (assuming DB has data).");
        System.out.println("...Found " + allEmployees.size() + " employees.");

        System.out.println("Result: getAllEmployee method works as expected. Test PASSED.\n");
    }
    
    @Test
    public void testDeleteEmployee_NonExistent_ShouldReturnFalse() {
        System.out.println("Running test: testDeleteEmployee_NonExistent_ShouldReturnFalse");
        System.out.println("Verifying delete operation on a non-existent employee...");
        
        // Arrange
        int nonExistentId = 999999;
        
        // Act
        boolean isDeleted = hrService.deleteEmployeeDetails(nonExistentId);
        
        // Assert
        assertFalse(isDeleted, "deleteEmployeeDetails should return false when trying to delete a non-existent ID.");
        
        System.out.println("Result: Correctly returned false for non-existent employee. Test PASSED.\n");
    }

    @AfterAll
    public static void teardown() throws SQLException {
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("--- Finished HRServiceTest: Closing database connection. ---");
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        System.out.println("Database connection closed.");
    }
}