/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author leniejoice
 */
public abstract class Person {
    int empID;
    private String lastName; 
    private String firstName;
    private Date empBirthday;
    private String empAddress;
    private String empPhoneNumber;
    private EmployeeStatus empStatus;
    private EmployeePosition empPosition;
    private Person empImmediateSupervisor;
    private String empSSS;
    private String empTIN;
    private long empPhilHealth;
    private long empPagibig;
    private double empBasicSalary;
    private double empRice;
    private double empPhone;
    private double empClothing;
    private double empMonthlyRate;
    private double empHourlyRate;
   
    
 
    
    // Constructor
    public Person(int empID, String lastName, String firstName, String empAddress, Date empBirthday,
                  String empPhoneNumber, String empSSS, String empTIN, long empPhilHealth,
                  long empPagibig, Person empImmediateSupervisor, EmployeeStatus empStatus,
                  EmployeePosition empPosition, double empBasicSalary, double empRice,
                  double empPhone, double empClothing, double empMonthlyRate, double empHourlyRate) { 
        
        this.empID = empID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.empAddress = empAddress;
        this.empBirthday = empBirthday;
        this.empPhoneNumber = empPhoneNumber;
        this.empSSS = empSSS;
        this.empTIN = empTIN;
        this.empPhilHealth = empPhilHealth;
        this.empPagibig = empPagibig;
        this.empImmediateSupervisor = empImmediateSupervisor;
        this.empStatus = empStatus;
        this.empPosition = empPosition;
        this.empBasicSalary = empBasicSalary;
        this.empRice = empRice;
        this.empPhone = empPhone;
        this.empClothing = empClothing;
        this.empMonthlyRate = empMonthlyRate;
        this.empHourlyRate = empHourlyRate;
    }
    
    

    // Getters
    public int getEmpID() {
        return empID;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmpAddress() {
        return empAddress;
    }

    public Date getEmpBirthday() {
        return empBirthday;
    }

    public String getEmpPhoneNumber() {
        return empPhoneNumber;
    }

    public String getEmpSSS() {
        return empSSS;
    }

    public String getEmpTIN() {
        return empTIN;
    }

    public long getEmpPhilHealth() {
        return empPhilHealth;
    }

    public long getEmpPagibig() {
        return empPagibig;
    }

    public Person getEmpImmediateSupervisor() {
        return empImmediateSupervisor;
    }

    public EmployeeStatus getEmpStatus() {
        return empStatus;
    }
    
        public EmployeePosition getEmpPosition() {
        return empPosition;
    }

    // Setters
    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setEmpAddress(String empAddress) {
        this.empAddress = empAddress;
    }

    public void setEmpBirthday(Date empBirthday) {
        this.empBirthday = empBirthday;
    }

    public void setEmpPhoneNumber(String empPhoneNumber) {
        this.empPhoneNumber = empPhoneNumber;
    }

    public void setEmpSSS(String empSSS) {
        this.empSSS = empSSS;
    }

    public void setEmpTIN(String empTIN) {
        this.empTIN = empTIN;
    }

    public void setEmpPhilHealth(long empPhilHealth) {
        this.empPhilHealth = empPhilHealth;
    }

    public void setEmpPagibig(long empPagibig) {
        this.empPagibig = empPagibig;
    }

    public void setEmpImmediateSupervisor(Person empImmediateSupervisor) {
        this.empImmediateSupervisor = empImmediateSupervisor;
    }

    public void setEmpStatus(EmployeeStatus empStatus) {
        this.empStatus = empStatus;
    }
    
        public void setEmpPosition(EmployeePosition empPosition) {
        this.empPosition = empPosition;
    }
        
     public double getEmpBasicSalary() {
        return empBasicSalary;
    }

    public double getEmpRice() {
        return empRice;
    }

    public double getEmpPhone() {
        return empPhone;
    }

    public double getEmpClothing() {
        return empClothing;
    }

    public double getEmpMonthlyRate() {
        return empMonthlyRate;
    }

    public double getEmpHourlyRate() {
        return empHourlyRate;
    }
    
    public void setEmpBasicSalary(double empBasicSalary) {
        this.empBasicSalary = empBasicSalary;
    }

    public void setEmpRice(double empRice) {
        this.empRice = empRice;
    }

    public void setEmpPhone(double empPhone) {
        this.empPhone = empPhone;
    }

    public void setEmpClothing(double empClothing) {
        this.empClothing = empClothing;
    }

    public void setEmpMonthlyRate(double empMonthlyRate) {
        this.empMonthlyRate = empMonthlyRate;
    }

    public void setEmpHourlyRate(double empHourlyRate) {
        this.empHourlyRate = empHourlyRate;
    }

    // CORRECT version
    public String getFormattedName() {
        // Start with the first name, which we assume is always present.
        String result = firstName != null ? firstName : "";

        // Only add the last name if it's not null and not empty.
        if (lastName != null && !lastName.isEmpty()) {
            result = result + " " + lastName;
        }

        return result.trim(); // Use trim() just in case firstName has spaces.

        }   
    
    // WRONG version    
    /*public String getFormattedName(){
        return (firstName+" "+lastName).trim();*/
        

    public String getFormattedBirthday(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return this.empBirthday != null ? formatter.format(this.empBirthday) : null;
    }
}
