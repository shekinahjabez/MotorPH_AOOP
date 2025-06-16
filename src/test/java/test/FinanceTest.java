/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Finance;
import com.payroll.domain.Person;
import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Unit Test for the Finance data model class.
 *
 * This test verifies that the Finance class can correctly hold and return data
 * through its constructors, setters, and getters.
 *
 * @author paulomolina
 */
public class FinanceTest {

    @BeforeAll
    public static void setup() {
        System.out.println("--- Starting FinanceTest ---");
        System.out.println("This test focuses on the Finance class's internal data handling (getters/setters).");
        System.out.println("No database connection is needed for this pure unit test.");
        System.out.println("----------------------------------------------------------");
    }

    @Test
    public void testFinanceObjectCreationAndDataRetrieval() {
        System.out.println("Running test: testFinanceObjectCreationAndDataRetrieval");
        System.out.println("Step 1: Creating a Finance object and setting its properties...");

        // Arrange: Create a new Finance object
        // We can use the empty constructor and then use setters.
        Finance financeRecord = new Finance();

        // Arrange: Define the test data
        int payrollId = 101;
        Date periodStart = new Date(); // Using current date for simplicity
        Date periodEnd = new Date(periodStart.getTime() + 14 * 24 * 60 * 60 * 1000); // 14 days later
        double hoursWorked = 80.5;
        double grossPay = 25000.00;
        double deduction = 3500.50;
        double netPay = 21499.50;
        int empId = 20001;
        String lastName = "FinanceTest";

        // Act: Use setters to populate the object
        financeRecord.setEmpID(empId);
        financeRecord.setLastName(lastName); // Inherited from Person
        financeRecord.setPayrollId(payrollId);
        financeRecord.setPayrollPeriodStart(periodStart);
        financeRecord.setPayrollPeriodEnd(periodEnd);
        financeRecord.setNumberOfHoursWorked(hoursWorked);
        financeRecord.setGrossPay(grossPay);
        financeRecord.setDeduction(deduction);
        financeRecord.setNetPay(netPay);

        System.out.println("...Object created and populated successfully.");
        System.out.println("Step 2: Verifying data using getters...");

        // Assert: Use getters to verify that the data was stored correctly
        assertEquals(empId, financeRecord.getEmpID(), "Employee ID should match the set value.");
        assertEquals(lastName, financeRecord.getLastName(), "Last Name should match the set value.");
        assertEquals(payrollId, financeRecord.getPayrollId(), "Payroll ID should match the set value.");
        assertEquals(periodStart, financeRecord.getPayrollPeriodStart(), "Payroll Start Date should match the set value.");
        assertEquals(periodEnd, financeRecord.getPayrollPeriodEnd(), "Payroll End Date should match the set value.");
        assertEquals(hoursWorked, financeRecord.getNumberOfHoursWorked(), "Hours Worked should match the set value.");
        assertEquals(grossPay, financeRecord.getGrossPay(), "Gross Pay should match the set value.");
        assertEquals(deduction, financeRecord.getDeduction(), "Deduction should match the set value.");
        assertEquals(netPay, financeRecord.getNetPay(), "Net Pay should match the set value.");

        System.out.println("...All getter values match the set values.");
        System.out.println("Result: Finance class correctly stores and retrieves data. Test PASSED.\n");
    }
    
    // We can add another test for the parameterized constructor if we want to be thorough.
    @Test
    public void testFinanceParameterizedConstructor() {
        System.out.println("Running test: testFinanceParameterizedConstructor");
        System.out.println("Step 1: Creating a Finance object using the full constructor...");

        // Arrange: Define test data for the constructor
        int empID = 20002;
        String lastName = "Constructor";
        String firstName = "Test";

        // Act: Create the object using the constructor that takes Person details
        Finance financeRecord = new Finance(empID, lastName, firstName, null, null, null, null, null, 0, 0, null, null, null, 50000, 0, 0, 0, 0, 0);

        System.out.println("...Object created successfully.");
        System.out.println("Step 2: Verifying data from the constructor...");

        // Assert: Check if the fields inherited from Person were set correctly
        assertEquals(empID, financeRecord.getEmpID(), "Constructor should set Employee ID correctly.");
        assertEquals(lastName, financeRecord.getLastName(), "Constructor should set Last Name correctly.");
        assertEquals(firstName, financeRecord.getFirstName(), "Constructor should set First Name correctly.");
        assertEquals(50000, financeRecord.getEmpBasicSalary(), "Constructor should set Basic Salary correctly.");
        
        System.out.println("Result: Parameterized constructor works as expected. Test PASSED.\n");
    }
}
