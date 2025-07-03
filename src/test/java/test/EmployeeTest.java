/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Employee;
import com.payroll.domain.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Date;
import java.util.Calendar;

/**
 * Unit Test for the Employee class.
 *
 * This test focuses on the internal logic of the Employee class, specifically the
 * calculation and formatting of hours worked.
 *
 * @author paulomolina
 */
public class EmployeeTest {

    // A fresh Employee object will be created for each test.
    private Employee employee;

    @BeforeAll
    public static void setupAll() {
        System.out.println("--- Starting EmployeeTest ---");
        System.out.println("This test focuses on the Employee class's internal calculation logic.");
        System.out.println("No database connection is needed for this pure unit test.");
        System.out.println("------------------------------------------------------------------");
    }
    
    @BeforeEach
    public void setupEach() {
        // This method runs before each @Test method, ensuring a clean slate.
        employee = new Employee();
    }

    // --- Tests for getHoursWorked() ---

    @Test
    public void testGetHoursWorked_StandardDay_ShouldDeductBreak() {
        System.out.println("Running test: testGetHoursWorked_StandardDay_ShouldDeductBreak");
        System.out.println("Verifying a standard 9-5 shift (8 hours) correctly results in 7 worked hours...");

        // Arrange
        employee.setTimeIn(LocalTime.of(9, 0));
        employee.setTimeOut(LocalTime.of(17, 0));

        // Act & Assert
        assertEquals(25200L, employee.getHoursWorked(), "A standard 8-hour shift should result in 7 hours worked (25200 seconds).");
        System.out.println("Result: Standard day calculation is correct. Test PASSED.\n");
    }

    @Test
    public void testGetHoursWorked_MidnightStart_ShouldNotDeductBreak() {
        System.out.println("Running test: testGetHoursWorked_MidnightStart_ShouldNotDeductBreak");
        System.out.println("Verifying a midnight start shift (8 hours) results in 8 worked hours...");

        // Arrange
        employee.setTimeIn(LocalTime.MIDNIGHT);
        employee.setTimeOut(LocalTime.of(8, 0));

        // Act & Assert
        assertEquals(28800L, employee.getHoursWorked(), "A shift starting at midnight should not have a break deducted.");
        System.out.println("Result: Midnight start calculation is correct. Test PASSED.\n");
    }

    @Test
    public void testGetHoursWorked_ShortShift_StillDeductsBreak() {
        System.out.println("Running test: testGetHoursWorked_ShortShift_StillDeductsBreak");
        System.out.println("Verifying a short shift (4 hours) incorrectly results in 3 worked hours (as per current logic)...");
        
        // Arrange
        employee.setTimeIn(LocalTime.of(9, 0));
        employee.setTimeOut(LocalTime.of(13, 0));

        // Act & Assert
        assertEquals(10800L, employee.getHoursWorked(), "A 4-hour shift should result in 3 hours worked with the current logic.");
        System.out.println("Result: Short shift calculation matches current (potentially flawed) logic. Test PASSED.\n");
    }

    // --- Tests for getFormattedHoursWorked() ---
    
    @Test
    public void testGetFormattedHoursWorked_WithMinutes() {
        System.out.println("Running test: testGetFormattedHoursWorked_WithMinutes");
        System.out.println("Verifying formatting for a shift with partial hours (e.g., 7.5 hours)...");

        // Arrange
        employee.setTimeIn(LocalTime.of(9, 0));
        employee.setTimeOut(LocalTime.of(17, 30));

        // Act & Assert
        assertEquals("7:30", employee.getFormattedHoursWorked(), "Should correctly format 7.5 hours as '7:30'.");
        System.out.println("Result: Formatting with minutes is correct. Test PASSED.\n");
    }

    // --- New Negative Test from Example ---

    @Test
    public void testGetHoursWorked_WhenTimeInIsNull_ShouldThrowException() {
        System.out.println("Running test: testGetHoursWorked_WhenTimeInIsNull_ShouldThrowException");
        System.out.println("Verifying that a NullPointerException is thrown if timeIn is null...");

        // Arrange
        employee.setTimeIn(null);
        employee.setTimeOut(LocalTime.of(17, 0));

        // Act & Assert
        // This asserts that the code inside the lambda () -> {} MUST throw a NullPointerException to pass.
        assertThrows(NullPointerException.class, () -> {
            employee.getHoursWorked();
        }, "getHoursWorked should throw NullPointerException when timeIn is null.");
        
        System.out.println("Result: Correctly threw NullPointerException. Test PASSED.\n");
    }

    @Test
    public void testGetHoursWorked_WhenTimeOutIsNull_ShouldThrowException() {
        System.out.println("Running test: testGetHoursWorked_WhenTimeOutIsNull_ShouldThrowException");
        System.out.println("Verifying that a NullPointerException is thrown if timeOut is null...");

        // Arrange
        employee.setTimeIn(LocalTime.of(9, 0));
        employee.setTimeOut(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            employee.getHoursWorked();
        }, "getHoursWorked should throw NullPointerException when timeOut is null.");
        
        System.out.println("Result: Correctly threw NullPointerException. Test PASSED.\n");
    }
}