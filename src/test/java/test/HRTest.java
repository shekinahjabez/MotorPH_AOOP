/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.HR;
// We still need this import to find the LeaveType CLASS
import com.payroll.subdomain.LeaveType; 

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Unit Test for the HR data model class.
 * This test is updated to correctly handle the LeaveType class.
 *
 * @author paulomolina
 */
public class HRTest {

    @BeforeAll
    public static void setup() {
        System.out.println("--- Starting HRTest ---");
        System.out.println("This test focuses on the HR class's internal data handling.");
        System.out.println("----------------------------------------------------------");
    }

    @Test
    public void testHRObjectCreationAndDataRetrieval() {
        System.out.println("Running test: testHRObjectCreationAndDataRetrieval");
        System.out.println("Step 1: Creating an HR object and setting its leave request properties...");

        // Arrange: Create a new HR object
        HR hrRecord = new HR();

        // **FIX IS HERE:** Create an actual LeaveType object
        LeaveType vacationLeave = new LeaveType();
        vacationLeave.setId(1); // Assuming an ID for vacation
        vacationLeave.setLeaveType("Vacation");

        // Arrange: Define the rest of the test data
        int empId = 30001;
        int leaveId = 501;
        String subject = "Vacation Leave Request";
        Date dateFrom = new Date();
        Date dateTo = new Date(dateFrom.getTime() + 5 * 24 * 60 * 60 * 1000); // 5 days later
        int totalDays = 5;
        String reason = "Family trip.";
        HR.LeaveStatus status = HR.LeaveStatus.PENDING;

        // Act: Use setters to populate the object
        hrRecord.setEmpID(empId);
        hrRecord.setLeaveId(leaveId);
        hrRecord.setSubject(subject);
        // Set the LeaveType OBJECT, not an enum value
        hrRecord.setLeaveType(vacationLeave); 
        hrRecord.setDateFrom(dateFrom);
        hrRecord.setDateTo(dateTo);
        hrRecord.setTotalDays(totalDays);
        hrRecord.setReason(reason);
        hrRecord.setStatus(status);

        System.out.println("...Object created and populated successfully.");
        System.out.println("Step 2: Verifying data using getters...");

        // Assert: Verify the data was stored correctly
        assertEquals(empId, hrRecord.getEmpID());
        assertEquals(leaveId, hrRecord.getLeaveId());
        // Assert that the retrieved LeaveType object is the same one we created
        assertEquals(vacationLeave, hrRecord.getLeaveType(), "Leave Type object should match.");
        // You can also check a property within the object for more detail
        assertEquals("Vacation", hrRecord.getLeaveType().getLeaveType(), "The string value of the leave type should be 'Vacation'.");
        assertEquals(status, hrRecord.getStatus());
        
        System.out.println("...All getter values match the set values.");
        System.out.println("Result: HR class correctly stores and retrieves data. Test PASSED.\n");
    }

    @Test
    public void testHRParameterizedConstructor() {
        System.out.println("Running test: testHRParameterizedConstructor");
        System.out.println("Step 1: Creating an HR object using the full constructor...");

        // **FIX IS HERE:** Create an actual LeaveType object for the constructor
        LeaveType sickLeave = new LeaveType();
        sickLeave.setId(2); // Assuming an ID for sick leave
        sickLeave.setLeaveType("Sick");

        // Arrange: Define other test data
        int empId = 30002;
        String lastName = "HR-Constructor";
        int leaveId = 502;
        HR.LeaveStatus status = HR.LeaveStatus.APPROVED;

        // Act: Create the object, passing in the sickLeave OBJECT
        HR hrRecord = new HR(empId, lastName, "Test", null, null, null, null, null, 0, 0, null, null, null, 0, 0, 0, 0, 0, 0, leaveId, "Subject Test", sickLeave, new Date(), new Date(), 1, "Reason Test", status);
        
        System.out.println("...Object created successfully.");
        System.out.println("Step 2: Verifying key data from the constructor...");

        // Assert: Check a few key fields
        assertEquals(empId, hrRecord.getEmpID());
        assertEquals(leaveId, hrRecord.getLeaveId());
        // Assert that the LeaveType object from the constructor is correct
        assertEquals(sickLeave, hrRecord.getLeaveType());
        assertEquals("Sick", hrRecord.getLeaveType().getLeaveType());
        
        System.out.println("Result: Parameterized constructor works as expected. Test PASSED.\n");
    }
}