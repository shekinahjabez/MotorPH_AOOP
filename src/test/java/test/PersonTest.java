/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import com.payroll.domain.Employee;
import com.payroll.domain.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for the abstract Person class.
 *
 * Since Person is abstract, we test its concrete methods by creating an instance
 * of a subclass, such as Employee. This test focuses on the logic within Person itself.
 *
 * @author paulomolina
 */
public class PersonTest {

    @BeforeAll
    public static void setup() {
        System.out.println("--- Starting PersonTest ---");
        System.out.println("Testing the logic of the abstract Person class via an Employee instance.");
        System.out.println("----------------------------------------------------------");
    }

    // A general test to ensure the getters and setters (the basic data holding part) work.
    @Test
    public void testPersonDataSetAndGet() {
        System.out.println("Running test: testPersonDataSetAndGet");
        System.out.println("Verifying that basic setters and getters work on a Person subclass...");

        // Arrange: We must use a concrete subclass like Employee.
        Person person = new Employee();

        // Act
        person.setEmpID(50001);
        person.setFirstName("Data");
        person.setLastName("Test");
        person.setEmpBasicSalary(50000.0);

        // Assert
        assertEquals(50001, person.getEmpID());
        assertEquals("Data", person.getFirstName());
        assertEquals("Test", person.getLastName());
        assertEquals(50000.0, person.getEmpBasicSalary());

        System.out.println("Result: Basic data integrity is confirmed. Test PASSED.\n");
    }

    // --- Tests for getFormattedName() logic ---

    @Test
    public void testGetFormattedName_WithValidNames() {
        System.out.println("Running test: testGetFormattedName_WithValidNames");
        System.out.println("Checking name formatting with standard first and last names...");

        // Arrange
        Person person = new Employee();
        person.setFirstName("John");
        person.setLastName("Doe");

        // Act
        String formattedName = person.getFormattedName();

        // Assert
        assertEquals("John Doe", formattedName, "Should combine first and last name with a space.");
        System.out.println("Result: Correctly formatted 'John Doe'. Test PASSED.\n");
    }

    @Test
    public void testGetFormattedName_WithNullLastName() {
        System.out.println("Running test: testGetFormattedName_WithNullLastName");
        System.out.println("Checking name formatting when last name is null...");

        // Arrange
        Person person = new Employee();
        person.setFirstName("Cher");
        person.setLastName(null);

        // Act
        String formattedName = person.getFormattedName();

        // Assert
        assertEquals("Cher", formattedName, "Should return only the first name without extra spaces.");
        System.out.println("Result: Correctly handled null last name. Test PASSED.\n");
    }

    // --- Tests for getFormattedBirthday() logic ---

    @Test
    public void testGetFormattedBirthday_WithValidDate() throws ParseException {
        System.out.println("Running test: testGetFormattedBirthday_WithValidDate");
        System.out.println("Checking birthday formatting for a valid date...");

        // Arrange
        Person person = new Employee();
        // We use SimpleDateFormat to create a specific, reliable Date object for testing.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse("25/12/1990");
        person.setEmpBirthday(birthDate);

        // Act
        String formattedBirthday = person.getFormattedBirthday();

        // Assert
        assertEquals("1990-12-25", formattedBirthday, "Date should be formatted as YYYY-MM-DD.");
        System.out.println("Result: Correctly formatted the date. Test PASSED.\n");
    }

    @Test
    public void testGetFormattedBirthday_WithNullDate() {
        System.out.println("Running test: testGetFormattedBirthday_WithNullDate");
        System.out.println("Checking birthday formatting when the date is null...");

        // Arrange
        Person person = new Employee();
        person.setEmpBirthday(null);

        // Act
        String formattedBirthday = person.getFormattedBirthday();

        // Assert
        assertNull(formattedBirthday, "Should return null when the birthday date is null.");
        System.out.println("Result: Correctly handled null date. Test PASSED.\n");
    }
}
