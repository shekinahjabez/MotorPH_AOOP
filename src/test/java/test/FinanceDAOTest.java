/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Finance;
import com.payroll.domain.Person;
import com.payroll.DAO.FinanceDAO;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit Test for the FinanceService class.
 *
 * This test class verifies that the service methods correctly interact with the
 * database to retrieve data, perform calculations, and save payroll information.
 *
 * @author paulomolina
 */
public class FinanceDAOTest {

    private static Connection connection;
    private static FinanceDAO financeService;

    // A test employee ID that we know exists and has payroll details.
    private static final int TEST_EMPLOYEE_ID = 10002;

    @BeforeAll
    public static void setup() throws SQLException {
        System.out.println("--- Starting FinanceServiceTest: Setting up database connection and service. ---");
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";
        connection = DriverManager.getConnection(url, user, password);
        financeService = new FinanceDAO(connection);
        System.out.println("Database connection and service initialized.");
        System.out.println("--------------------------------------------------------------------------------");
    }

    @Test
    public void testGetByEmpID_Found() throws SQLException {
        System.out.println("Running test: testGetByEmpID_Found");
        System.out.println("Verifying retrieval of existing employee payroll details...");

        // Act
        Person person = financeService.getByEmpID(TEST_EMPLOYEE_ID);

        // Assert
        assertNotNull(person, "Person object should not be null for an existing employee.");
        assertEquals(TEST_EMPLOYEE_ID, person.getEmpID(), "The retrieved employee ID should match the requested ID.");
        assertTrue(person.getEmpBasicSalary() > 0, "Retrieved employee should have a basic salary.");

        System.out.println("Result: Successfully retrieved employee details. Test PASSED.\n");
    }

    @Test
    public void testGetByEmpID_NotFound() throws SQLException {
        System.out.println("Running test: testGetByEmpID_NotFound");
        System.out.println("Verifying behavior when employee ID does not exist...");

        // Arrange
        int nonExistentId = 99999;

        // Act
        Person person = financeService.getByEmpID(nonExistentId);

        // Assert
        assertNull(person, "Should return null for a non-existent employee ID.");

        System.out.println("Result: Correctly returned null for non-existent employee. Test PASSED.\n");
    }

    @Test
    public void testCalculateSssContribution_ValidSalary() {
        System.out.println("Running test: testCalculateSssContribution_ValidSalary");
        System.out.println("Verifying SSS contribution calculation against the database lookup table...");

        // Arrange: Using real data. Employee 10004 has a salary of 50825.
        // Based on the 2023 SSS table, the employee share for this salary is 1125.00.
        double salary = 50825.00;
        float expectedContribution = 1125.00f;

        // Act
        float actualContribution = financeService.calculateSssContribution(salary);

        // Assert
        assertEquals(expectedContribution, actualContribution, 0.01, "SSS contribution for a salary of 50825.00 should be 1125.00.");

        System.out.println("Result: SSS contribution calculated correctly based on real data. Test PASSED.\n");
    }

    @Test
    public void testUpdateAndRetrievePayrollDetails() throws SQLException {
        System.out.println("Running test: testUpdateAndRetrievePayrollDetails");
        System.out.println("Verifying that employee payroll details can be updated and retrieved...");

        // --- 1. ARRANGE (Get original details for employee 10002) ---
        Person originalDetails = financeService.getByEmpID(TEST_EMPLOYEE_ID);
        assertNotNull(originalDetails, "Precondition failed: Cannot find employee to test update.");
        // Based on your data: rice_subsidy is 1500, sss is "49-2959312-6"
        double originalRiceSubsidy = originalDetails.getEmpRice();
        String originalSSS = originalDetails.getEmpSSS();

        // --- 2. ACT (Update the details) ---
        System.out.println("Step 1: Updating employee details...");
        double newRiceSubsidy = 2500.0;
        String newSSS = "SSS-TEST-UPDATE";
        originalDetails.setEmpRice(newRiceSubsidy);
        originalDetails.setEmpSSS(newSSS);
        financeService.updatePayrollDetails(originalDetails);
        System.out.println("...Update operation complete.");

        // --- 3. ASSERT (Retrieve and check if update was successful) ---
        System.out.println("Step 2: Retrieving updated details to verify...");
        Person updatedDetails = financeService.getByEmpID(TEST_EMPLOYEE_ID);
        assertEquals(newRiceSubsidy, updatedDetails.getEmpRice(), "Rice subsidy should have been updated.");
        assertEquals(newSSS, updatedDetails.getEmpSSS(), "SSS number should have been updated.");
        System.out.println("...Verification successful.");

        // --- 4. CLEANUP (Revert to original details) ---
        System.out.println("Step 3: Reverting changes for test consistency...");
        originalDetails.setEmpRice(originalRiceSubsidy);
        originalDetails.setEmpSSS(originalSSS);
        financeService.updatePayrollDetails(originalDetails);
        System.out.println("...Cleanup complete.");

        System.out.println("Result: Full Update-Retrieve-Revert cycle successful. Test PASSED.\n");
    }

    @AfterAll
    public static void teardown() throws SQLException {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("--- Finished FinanceServiceTest: Closing database connection. ---");
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        System.out.println("Database connection closed.");
    }
}