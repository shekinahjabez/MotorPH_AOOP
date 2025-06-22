/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Employee;
import com.payroll.domain.HR;
import com.payroll.domain.LeaveBalance;
import com.payroll.subdomain.LeaveType;
import com.payroll.services.EmployeeService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Unit Test for the EmployeeService class.
 *
 * This test class verifies that the service methods correctly interact with the
 * database to perform CRUD operations for leave balances, leave requests, and attendance.
 *
 * @author paulomolina
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeServiceTest {

    private static Connection connection;
    private static EmployeeService employeeService;

    // A test employee ID that we know exists in our database.
    private static final int TEST_EMPLOYEE_ID = 10002;

    @BeforeAll
    public static void setup() throws SQLException {
        System.out.println("--- Starting EmployeeServiceTest: Setting up database connection and service. ---");
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";
        connection = DriverManager.getConnection(url, user, password);
        employeeService = new EmployeeService(connection);
        System.out.println("Database connection and service initialized.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    @Order(1)
    public void testLeaveBalance_CRUD_Cycle() {
        System.out.println("Running test: testLeaveBalance_CRUD_Cycle");

        // --- 1. CLEANUP (to ensure a clean state) ---
        System.out.println("Step 1: Cleaning up any old test data for leave balance...");
        employeeService.deleteLeaveBalance(TEST_EMPLOYEE_ID);

        // --- 2. CREATE (using saveLeaveBalance) ---
        System.out.println("Step 2: Creating a new leave balance record...");
        LeaveBalance newBalance = new LeaveBalance();
        newBalance.setEmpID(TEST_EMPLOYEE_ID);
        newBalance.setTaken(5);
        newBalance.setAvailable(20);
        newBalance.setTotal(25);
        assertDoesNotThrow(() -> employeeService.saveLeaveBalance(newBalance), "saveLeaveBalance should not throw an exception.");
        System.out.println("...Create operation complete.");

        // --- 3. READ (using getLeaveBalance) ---
        System.out.println("Step 3: Reading the created leave balance record...");
        LeaveBalance retrievedBalance = employeeService.getLeaveBalance(TEST_EMPLOYEE_ID);
        assertNotNull(retrievedBalance, "Retrieved balance should not be null.");
        assertEquals(5, retrievedBalance.getTaken(), "Read 'taken' value is incorrect.");
        assertEquals(20, retrievedBalance.getAvailable(), "Read 'available' value is incorrect.");
        System.out.println("...Read operation verified successfully.");

        // --- 4. UPDATE (using updateLeaveBalance) ---
        System.out.println("Step 4: Updating the leave balance record...");
        retrievedBalance.setTaken(7);
        retrievedBalance.setAvailable(18);
        employeeService.updateLeaveBalance(retrievedBalance);
        LeaveBalance updatedBalance = employeeService.getLeaveBalance(TEST_EMPLOYEE_ID);
        assertEquals(7, updatedBalance.getTaken(), "Updated 'taken' value is incorrect.");
        assertEquals(18, updatedBalance.getAvailable(), "Updated 'available' value is incorrect.");
        System.out.println("...Update operation verified successfully.");

        // --- 5. DELETE (using deleteLeaveBalance) ---
        System.out.println("Step 5: Deleting the test leave balance record...");
        employeeService.deleteLeaveBalance(TEST_EMPLOYEE_ID);
        LeaveBalance deletedBalance = employeeService.getLeaveBalance(TEST_EMPLOYEE_ID);
        assertEquals(0, deletedBalance.getEmpID(), "Balance should be empty (default state) after deletion.");
        System.out.println("...Delete operation verified successfully.");

        System.out.println("Result: LeaveBalance CRUD cycle completed successfully. Test PASSED.\n");
    }

    @Test
    @Order(2)
    public void testLeaveRequest_CRUD_Cycle() {
        System.out.println("Running test: testLeaveRequest_CRUD_Cycle");
        
        // --- 1. CLEANUP ---
        System.out.println("Step 1: Cleaning up old leave requests for test employee...");
        employeeService.deleteLeaveRequestbyEmpID(TEST_EMPLOYEE_ID);

        // --- 2. CREATE (using addLeaveRequest) ---
        System.out.println("Step 2: Adding a new leave request...");
        HR newRequest = new HR();
        newRequest.setEmpID(TEST_EMPLOYEE_ID);
        newRequest.setSubject("Test Vacation");
        newRequest.setTotalDays(3);
        newRequest.setDateFrom(new Date());
        newRequest.setDateTo(new Date());
        newRequest.setReason("Test Reason");
        newRequest.setStatus(HR.LeaveStatus.PENDING);
        // Assuming leave type ID 1 exists and is 'Vacation' from a previous test or setup
        LeaveType vacationType = employeeService.getLeaveTypeById(1); 
        assertNotNull(vacationType, "Precondition failed: Leave Type with ID 1 must exist.");
        newRequest.setLeaveType(vacationType);

        HR savedRequest = employeeService.addLeaveRequest(newRequest);
        assertTrue(savedRequest.getLeaveId() > 0, "Saved leave request should have a generated ID.");
        System.out.println("...Leave request created with ID: " + savedRequest.getLeaveId());

        // --- 3. READ (using getLeavesByEmployee) ---
        System.out.println("Step 3: Reading leave requests for the employee...");
        List<HR> requests = employeeService.getLeavesByEmployee(TEST_EMPLOYEE_ID);
        assertEquals(1, requests.size(), "Should retrieve one leave request.");
        assertEquals("Test Vacation", requests.get(0).getSubject(), "Retrieved request subject is incorrect.");
        System.out.println("...Read operation verified successfully.");

        // --- 4. UPDATE (using updateLeaveRequestStatus) ---
        System.out.println("Step 4: Updating leave request status to APPROVED...");
        employeeService.updateLeaveRequestStatus(HR.LeaveStatus.APPROVED, savedRequest.getLeaveId());
        // Verify update by fetching all APPROVED requests
        List<HR> approvedRequests = employeeService.getAllLeaveRequestByStatus(HR.LeaveStatus.APPROVED);
        boolean foundUpdated = approvedRequests.stream().anyMatch(r -> r.getLeaveId() == savedRequest.getLeaveId());
        assertTrue(foundUpdated, "Updated request should be found in the APPROVED list.");
        System.out.println("...Update operation verified successfully.");

        // --- 5. DELETE (using deleteLeaveRequest) ---
        System.out.println("Step 5: Deleting the test leave request...");
        employeeService.deleteLeaveRequest(savedRequest.getLeaveId());
        List<HR> requestsAfterDelete = employeeService.getLeavesByEmployee(TEST_EMPLOYEE_ID);
        assertTrue(requestsAfterDelete.isEmpty(), "Leave requests list should be empty after deletion.");
        System.out.println("...Delete operation verified successfully.");
        
        System.out.println("Result: Leave Request CRUD cycle completed successfully. Test PASSED.\n");
    }

    @Test
    @Order(3)
    public void testAttendance_TimeInAndOut_Flow() throws SQLException {
        System.out.println("Running test: testAttendance_TimeInAndOut_Flow");

        // --- 1. CLEANUP (Ensures a clean slate for today's date) ---
        System.out.println("Step 1: Cleaning up any existing attendance record for today...");
        try (PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM public.employee_hours WHERE employee_id = ? AND date = CURRENT_DATE")) {
            deleteStmt.setInt(1, TEST_EMPLOYEE_ID);
            deleteStmt.executeUpdate();
        }

        // --- 2. TIME IN ---
        System.out.println("Step 2: Performing Time-In...");
        Employee employee = new Employee();
        employee.setEmpID(TEST_EMPLOYEE_ID);
        employeeService.timeIn(employee);
        
        // Assert that time-in was recorded
        assertTrue(employeeService.hasTimeInToday(TEST_EMPLOYEE_ID), "hasTimeInToday should be true after time-in.");
        assertFalse(employeeService.hasTimeOutToday(TEST_EMPLOYEE_ID), "hasTimeOutToday should be false after time-in.");
        System.out.println("...Time-In successful and verified.");

        // --- 3. TIME OUT ---
        System.out.println("Step 3: Performing Time-Out...");
        employeeService.timeOut(employee);
        
        // Assert that time-out was recorded
        assertTrue(employeeService.hasTimeOutToday(TEST_EMPLOYEE_ID), "hasTimeOutToday should be true after time-out.");
        System.out.println("...Time-Out successful and verified.");

        System.out.println("Result: Attendance Time-In/Out flow works correctly. Test PASSED.\n");
    }

    @AfterAll
    public static void teardown() throws SQLException {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("--- Finished EmployeeServiceTest: Closing database connection. ---");
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        System.out.println("Database connection closed.");
    }
}