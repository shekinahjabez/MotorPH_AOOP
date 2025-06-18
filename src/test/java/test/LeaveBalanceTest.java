/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.HR;
import com.payroll.domain.LeaveBalance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit Test for the LeaveBalance class.
 *
 * This test focuses on verifying the logic of the updateLeaveBalance method
 * under various scenarios.
 *
 * @author paulomolina
 */
public class LeaveBalanceTest {

    @BeforeAll
    public static void setup() {
        System.out.println("--- Starting LeaveBalanceTest ---");
        System.out.println("This test focuses on the LeaveBalance class's calculation logic.");
        System.out.println("----------------------------------------------------------");
    }

    @Test
    public void testInitialLeaveBalanceState() {
        System.out.println("Running test: testInitialLeaveBalanceState");
        System.out.println("Checking the default values of a new LeaveBalance object...");

        // Arrange & Act
        LeaveBalance balance = new LeaveBalance();

        // Assert
        assertEquals(25, balance.getTotal(), "Default total leave should be 25.");
        assertEquals(25, balance.getAvailable(), "Default available leave should be 25.");
        assertEquals(0, balance.getTaken(), "Default taken leave should be 0.");

        System.out.println("Result: Initial state is correct. Test PASSED.\n");
    }

    @Test
    public void testUpdateWithSingleApprovedLeave() {
        System.out.println("Running test: testUpdateWithSingleApprovedLeave");
        System.out.println("Updating balance with one 5-day approved leave...");

        // Arrange
        LeaveBalance balance = new LeaveBalance();
        List<HR> leaveRequests = new ArrayList<>();
        HR approvedLeave = new HR();
        approvedLeave.setTotalDays(5);
        approvedLeave.setStatus(HR.LeaveStatus.APPROVED);
        leaveRequests.add(approvedLeave);

        // Act
        balance.updateLeaveBalance(leaveRequests);

        // Assert
        assertEquals(5, balance.getTaken(), "Taken leave should be 5.");
        assertEquals(20, balance.getAvailable(), "Available leave should be 20 (25 - 5).");
        assertEquals(25, balance.getTotal(), "Total leave should remain 25.");

        System.out.println("Result: Balance updated correctly. Test PASSED.\n");
    }
    
    @Test
    public void testUpdateWithSinglePendingLeave() {
        System.out.println("Running test: testUpdateWithSinglePendingLeave");
        System.out.println("Updating balance with one 3-day pending leave (should be counted)...");

        // Arrange
        LeaveBalance balance = new LeaveBalance();
        List<HR> leaveRequests = new ArrayList<>();
        HR pendingLeave = new HR();
        pendingLeave.setTotalDays(3);
        pendingLeave.setStatus(HR.LeaveStatus.PENDING);
        leaveRequests.add(pendingLeave);

        // Act
        balance.updateLeaveBalance(leaveRequests);

        // Assert
        assertEquals(3, balance.getTaken(), "Taken leave should count pending requests, so it should be 3.");
        assertEquals(22, balance.getAvailable(), "Available leave should be 22 (25 - 3).");

        System.out.println("Result: Balance updated correctly for pending leave. Test PASSED.\n");
    }

    @Test
    public void testUpdateWithDeclinedLeave() {
        System.out.println("Running test: testUpdateWithDeclinedLeave");
        System.out.println("Updating balance with one 7-day declined leave (should be ignored)...");

        // Arrange
        LeaveBalance balance = new LeaveBalance();
        List<HR> leaveRequests = new ArrayList<>();
        HR declinedLeave = new HR();
        declinedLeave.setTotalDays(7);
        // **IMPORTANT:** Your code checks for the STRING name 'DECLINED', so we use .name()
        // If your code was `!l.getStatus().equals(LeaveStatus.DECLINED)`, we would just set the status.
        // Since it is `!l.getStatus().equals(LeaveStatus.DECLINED.name())`, this test confirms that logic.
        declinedLeave.setStatus(HR.LeaveStatus.DECLINED);
        leaveRequests.add(declinedLeave);

        // Act
        balance.updateLeaveBalance(leaveRequests);

        // Assert
        assertEquals(0, balance.getTaken(), "Declined leave should not increase taken days.");
        assertEquals(25, balance.getAvailable(), "Available leave should remain 25.");

        System.out.println("Result: Declined leave was correctly ignored. Test PASSED.\n");
    }

    @Test
    public void testUpdateWithMixedStatusLeaves() {
        System.out.println("Running test: testUpdateWithMixedStatusLeaves");
        System.out.println("Updating with a mix of approved, pending, and declined leaves...");

        // Arrange
        LeaveBalance balance = new LeaveBalance();
        List<HR> leaveRequests = new ArrayList<>();

        HR approvedLeave = new HR();
        approvedLeave.setTotalDays(5);
        approvedLeave.setStatus(HR.LeaveStatus.APPROVED);
        leaveRequests.add(approvedLeave);

        HR pendingLeave = new HR();
        pendingLeave.setTotalDays(2);
        pendingLeave.setStatus(HR.LeaveStatus.PENDING);
        leaveRequests.add(pendingLeave);

        HR declinedLeave = new HR();
        declinedLeave.setTotalDays(10);
        declinedLeave.setStatus(HR.LeaveStatus.DECLINED); // Corrected to use the inner enum
        leaveRequests.add(declinedLeave);

        // Act
        balance.updateLeaveBalance(leaveRequests);

        // Assert: It should sum the approved (5) and pending (2) days, ignoring the declined (10) days.
        assertEquals(7, balance.getTaken(), "Taken leave should be 7 (5 approved + 2 pending).");
        assertEquals(18, balance.getAvailable(), "Available leave should be 18 (25 - 7).");

        System.out.println("Result: Mixed list was calculated correctly. Test PASSED.\n");
    }

    @Test
    public void testUpdateWithEmptyLeaveList() {
        System.out.println("Running test: testUpdateWithEmptyLeaveList");
        System.out.println("Updating balance with an empty list of leaves...");

        // Arrange
        LeaveBalance balance = new LeaveBalance();
        List<HR> emptyList = new ArrayList<>();

        // Act
        balance.updateLeaveBalance(emptyList);

        // Assert
        assertEquals(0, balance.getTaken(), "Taken leave should remain 0 for an empty list.");
        assertEquals(25, balance.getAvailable(), "Available leave should remain 25 for an empty list.");

        System.out.println("Result: Empty list was handled gracefully. Test PASSED.\n");
    }
}