/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Employee;
import com.payroll.domain.IT;
import com.payroll.domain.Person;
import com.payroll.services.HRService;
import com.payroll.services.ITService;
import com.payroll.subdomain.EmployeePosition; 
import com.payroll.subdomain.EmployeeStatus; 
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit Test for the ITService class.
 *
 * This test class verifies the authentication and user account management
 * functionalities of the ITService.
 *
 * @author paulomolina
 */
public class ITServiceTest {

    private static Connection connection;
    private static ITService itService;
    private static HRService hrService; // Needed for creating/deleting employee details
    

    // Use an employee ID that we know has an account in the test DB
    private static final int EXISTING_ACCOUNT_EMP_ID = 10002;
    // A temporary ID for our create/delete test
    private static final int TEMP_EMP_ID_FOR_ACCOUNT = 77777;

    @BeforeAll
    public static void setup() throws SQLException {
        System.out.println("--- Starting ITServiceTest: Setting up database connection and services. ---");
        String url = "jdbc:postgresql://localhost:5432/AOOP";
        String user = "postgres";
        String password = "martin27";
        connection = DriverManager.getConnection(url, user, password);
        
        // Initialize BOTH services
        itService = new ITService(connection);
        hrService = new HRService(connection); // <-- ADD THIS LINE
        
        System.out.println("Database connection and services initialized.");
        System.out.println("--------------------------------------------------------------------------");
    }

    // --- Authentication Tests ---

    @Test
    public void testGetUserAccount_ValidLogin() {
        System.out.println("Running test: testGetUserAccount_ValidLogin");
        System.out.println("Verifying login with correct credentials...");
        
        
        String validUsername = "christian";
        String validPassword = "123";

        // Act
        IT userAccount = itService.getUserAccount(validUsername, validPassword);

        // Assert
        assertNotNull(userAccount, "A valid account should be returned for correct credentials.");
        assertEquals(EXISTING_ACCOUNT_EMP_ID, userAccount.getEmpID(), "The employee ID of the logged-in user should be correct.");
        
        System.out.println("Result: Valid login successful. Test PASSED.\n");
    }
    
    

    @Test
    public void testGetUserAccount_InvalidPassword() {
        System.out.println("Running test: testGetUserAccount_InvalidPassword");
        System.out.println("Verifying login with an incorrect password...");

        // Arrange
        String validUsername = "employee10002";
        String invalidPassword = "wrongPassword";

        // Act
        IT userAccount = itService.getUserAccount(validUsername, invalidPassword);

        // Assert
        assertNull(userAccount, "No account should be returned for an incorrect password.");

        System.out.println("Result: Correctly failed login with invalid password. Test PASSED.\n");
    }

    @Test
    public void testGetUserAccount_UnknownUser() {
        System.out.println("Running test: testGetUserAccount_UnknownUser");
        System.out.println("Verifying login with a non-existent username...");

        // Arrange
        String unknownUsername = "noSuchUser";
        String anyPassword = "password123";

        // Act
        IT userAccount = itService.getUserAccount(unknownUsername, anyPassword);

        // Assert
        assertNull(userAccount, "No account should be returned for an unknown username.");

        System.out.println("Result: Correctly failed login with unknown user. Test PASSED.\n");
    }

    // --- User Account CRUD Cycle Test ---

    @Test
    @Order(4)
    public void testUserAccount_CRUD_Cycle() throws SQLException {
        System.out.println("Running test: testUserAccount_CRUD_Cycle");

        // This test now requires a generated ID, so we'll clean up at the end.
        int generatedEmpId = 0;

        try {
            // --- 1. CREATE (A valid employee first) ---
            System.out.println("Step 1: Creating a new employee record...");

            Person newEmployee = new Employee();
            newEmployee.setFirstName("Account");
            newEmployee.setLastName("TestUser");
            newEmployee.setEmpAddress("123 Test St");
            newEmployee.setEmpBirthday(new java.util.Date());
            newEmployee.setEmpPhoneNumber("555-0000");
            newEmployee.setEmpSSS("SSS-CRUD-TEST");
            newEmployee.setEmpTIN("TIN-CRUD-TEST");
            newEmployee.setEmpPagibig(98765L);
            newEmployee.setEmpPhilHealth(56789L);
            newEmployee.setEmpBasicSalary(50000);
            EmployeePosition pos = hrService.getPositionById(1);
            EmployeeStatus stat = hrService.getStatusById(1);
            assertNotNull(pos, "Precondition: Position ID 1 must exist.");
            assertNotNull(stat, "Precondition: Status ID 1 must exist.");
            newEmployee.setEmpPosition(pos);
            newEmployee.setEmpStatus(stat);

            Person createdEmployee = hrService.addEmployeeDetails(newEmployee);
            generatedEmpId = createdEmployee.getEmpID(); // Capture the generated ID
            assertTrue(generatedEmpId > 0, "addEmployeeDetails should return an employee with a generated ID.");
            System.out.println("...Employee created with generated ID: " + generatedEmpId);

            // --- 2. CREATE (The account for the new employee) ---
            System.out.println("Step 2: Creating the user account...");
            IT newAccount = new IT();
            newAccount.setEmpID(generatedEmpId);
            newAccount.setEmpUserName("new_crud_user");
            newAccount.setEmpPassword("new_crud_pass");

            itService.saveUserAccount(newAccount, createdEmployee);
            assertTrue(newAccount.getAccountID() > 0, "Saved account should have a generated ID.");
            System.out.println("...Account created successfully.");

            // --- 3. READ ---
            System.out.println("Step 3: Reading the created user account...");
            IT retrievedAccount = itService.getByEmpID(generatedEmpId);
            assertNotNull(retrievedAccount, "Should find the newly created account.");
            assertEquals("new_crud_user", retrievedAccount.getEmpUserName());
            System.out.println("...Read operation verified successfully.");

        } finally {
            // --- 4. CLEANUP (This runs even if the test fails) ---
            if (generatedEmpId > 0) {
                System.out.println("Step 4: Cleaning up test data for employee ID: " + generatedEmpId);
                itService.deleteEmpAccount(generatedEmpId);
                hrService.deleteEmployeeDetails(generatedEmpId);
                System.out.println("...Cleanup successful.");
            }
        }

        System.out.println("Result: User Account CRUD cycle completed successfully. Test PASSED.\n");
    }


    @AfterAll
    public static void teardown() throws SQLException {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("--- Finished ITServiceTest: Closing database connection. ---");
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        System.out.println("Database connection closed.");
    }
}
