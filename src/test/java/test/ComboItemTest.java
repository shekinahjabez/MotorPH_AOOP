/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.subdomain.ComboItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for the ComboItem utility class.
 *
 * This test verifies the custom 'equals()' logic and the 'toString()' override
 * to ensure the class behaves correctly, especially when used in UI components.
 *
 * @author paulomolina
 */
public class ComboItemTest {

    @BeforeAll
    public static void setup() {
        System.out.println("--- Starting ComboItemTest ---");
        System.out.println("Testing the logic of the ComboItem helper class.");
        System.out.println("------------------------------------------------");
    }

    @Test
    public void testEquals_WithSameValue_ShouldBeEqual() {
        System.out.println("Running test: testEquals_WithSameValue_ShouldBeEqual");
        System.out.println("Verifying that two ComboItems with the same value are considered equal...");

        // Arrange
        ComboItem item1 = new ComboItem(1, "Administrator");
        ComboItem item2 = new ComboItem(99, "Administrator"); // Different key, same value

        // Assert
        assertTrue(item1.equals(item2), "Items with the same value should be equal.");
        // We can also test the other way around for symmetry
        assertTrue(item2.equals(item1), "Equality should be symmetric.");

        System.out.println("Result: Equality logic for same value is correct. Test PASSED.\n");
    }

    @Test
    public void testEquals_WithDifferentValue_ShouldNotBeEqual() {
        System.out.println("Running test: testEquals_WithDifferentValue_ShouldNotBeEqual");
        System.out.println("Verifying that two ComboItems with different values are not equal...");

        // Arrange
        ComboItem item1 = new ComboItem(1, "Administrator");
        ComboItem item2 = new ComboItem(1, "Employee"); // Same key, different value

        // Assert
        assertFalse(item1.equals(item2), "Items with different values should not be equal.");

        System.out.println("Result: Inequality logic is correct. Test PASSED.\n");
    }

    @Test
    public void testEquals_WithNullObject_ShouldBeFalse() {
        System.out.println("Running test: testEquals_WithNullObject_ShouldBeFalse");
        System.out.println("Verifying that comparing a ComboItem to null returns false...");

        // Arrange
        ComboItem item1 = new ComboItem(1, "Administrator");

        // Assert
        assertFalse(item1.equals(null), "Comparing to null should always return false.");
        
        System.out.println("Result: Null comparison is handled correctly. Test PASSED.\n");
    }
    
    @Test
    public void testEquals_WithDifferentObjectType_ShouldBeFalse() {
        System.out.println("Running test: testEquals_WithDifferentObjectType_ShouldBeFalse");
        System.out.println("Verifying that comparing a ComboItem to a different object type returns false...");

        // Arrange
        ComboItem item1 = new ComboItem(1, "Administrator");
        String notAComboItem = "Administrator";

        // Assert
        assertFalse(item1.equals(notAComboItem), "Comparing to a different object type (e.g., a String) should return false.");
        
        System.out.println("Result: Different object type comparison is handled correctly. Test PASSED.\n");
    }

    @Test
    public void testToString_ShouldReturnValue() {
        System.out.println("Running test: testToString_ShouldReturnValue");
        System.out.println("Verifying that the toString() method returns the 'value' field...");

        // Arrange
        String expectedValue = "Administrator";
        ComboItem item = new ComboItem(1, expectedValue);

        // Act
        String actualToStringResult = item.toString();

        // Assert
        assertEquals(expectedValue, actualToStringResult, "toString() should return the String value of the item.");
        
        System.out.println("Result: toString() method works as expected. Test PASSED.\n");
    }
}
