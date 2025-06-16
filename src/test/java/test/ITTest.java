/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Employee; // Import the Employee class
import com.payroll.domain.HR;
import com.payroll.domain.IT;
import com.payroll.domain.Person; // Keep this import for type declarations
import com.payroll.subdomain.UserRole;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for the IT data model class.
 * This test is updated to correctly handle the abstract Person class.
 *
 * @author paulomolina
 */
public class ITTest {

    @BeforeAll
    public static void setup() {
        System.out.println("--- Starting ITTest ---");
        System.out.println("This test focuses on the IT class's internal data handling.");
        System.out.println("----------------------------------------------------------");
    }

    @Test
    public void testITObjectCreationAndDataRetrieval() {
        System.out.println("Running test: testITObjectCreationAndDataRetrieval");
        System.out.println("Step 1: Creating an IT object and its related objects...");

        // Arrange: Create a new IT object
        IT itAccount = new IT();

        // **FIX IS HERE:** Create an Employee object instead of a Person object.
        // The `empDetails` field is of type Person, so it can hold an Employee.
        Employee employeeDetails = new Employee(); 
        employeeDetails.setEmpID(40001);
        employeeDetails.setFirstName("IT");
        employeeDetails.setLastName("TestUser");

        // Assuming UserRole is a simple class. If it's an enum, this needs to change.
        UserRole adminRole = new UserRole();

        HR leaveRecord = new HR();
        leaveRecord.setLeaveId(801);
        
        // Arrange: Define simple data for the IT object itself
        int accountId = 701;
        String username = "it.testuser";
        String password = "securePassword!@#";

        // Act: Use setters to populate the IT object
        itAccount.setAccountID(accountId);
        itAccount.setEmpUserName(username);
        itAccount.setEmpPassword(password);
        itAccount.setEmpDetails(employeeDetails); // Set the Employee object
        itAccount.setUserRole(adminRole);
        itAccount.setLeaveDetail(leaveRecord);

        System.out.println("...Object created and populated successfully.");
        System.out.println("Step 2: Verifying data using getters...");

        // Assert: Verify all data, including the composed objects
        assertEquals(accountId, itAccount.getAccountID(), "Account ID should match.");
        assertEquals(username, itAccount.getEmpUserName(), "Username should match.");
        
        // Assert that the composed objects are the same ones we created
        assertEquals(employeeDetails, itAccount.getEmpDetails(), "Employee Details object should match.");
        
        // We can even check a value inside the composed object for extra certainty
        assertEquals("TestUser", itAccount.getEmpDetails().getLastName(), "Last name inside the composed Person object should be correct.");

        System.out.println("...All getter values match the set values.");
        System.out.println("Result: IT class correctly stores and retrieves data. Test PASSED.\n");
    }
}