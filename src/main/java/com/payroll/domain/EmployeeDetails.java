/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 *
 * @author leniejoice
 */
public class EmployeeDetails {


    //employee details
    private int empID;
    private String lastName; 
    private String firstName;
    private Date empBirthday;
    private String empAddress;
    private String empPhoneNumber;
    private EmployeeStatus empStatus;
    private EmployeePosition empPosition;
    private EmployeeDetails empImmediateSupervisor;
    
    //Deductions
    private String empSSS;
    private long empPhilHealth;
    private String empTIN;
    private long empPagibig;

    
    //Benefits
    private double empBasicSalary;
    private double empRice;
    private double empPhone;
    private double empClothing;
    
    //Rate
    private double empMonthlyRate;
    private double empHourlyRate;

    public double getBiMonthlyRate() {
        return biMonthlyRate;
    }

    public void setBiMonthlyRate(double biMonthlyRate) {
        this.biMonthlyRate = biMonthlyRate;
    }
    private double biMonthlyRate;
            
            
    //end

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }
    


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getEmpBirthday() {
        return empBirthday;
    }

    public void setEmpBirthday(Date empBirthday) {
        this.empBirthday = empBirthday;
    }

    public String getEmpAddress() {
        return empAddress;
    }

    public void setEmpAddress(String empAddress) {
        this.empAddress = empAddress;
    }

    public String getEmpPhoneNumber() {
        return empPhoneNumber;
    }

    public void setEmpPhoneNumber(String empPhoneNumber) {
        this.empPhoneNumber = empPhoneNumber;
    }

    public String getEmpSSS() {
        return empSSS;
    }

    public void setEmpSSS(String empSSS) {
        this.empSSS = empSSS;
    }

    public long getEmpPhilHealth() {
        return empPhilHealth;
    }

    public void setEmpPhilHealth(long empPhilHealth) {
        this.empPhilHealth = empPhilHealth;
    }

    public String getEmpTIN() {
        return empTIN;
    }

    public void setEmpTIN(String empTIN) {
        this.empTIN = empTIN;
    }

    public long getEmpPagibig() {
        return empPagibig;
    }

    public void setEmpPagibig(long empPagibig) {
        this.empPagibig = empPagibig;
    }

    public EmployeeStatus getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(EmployeeStatus empStatus) {
        this.empStatus = empStatus;
    }

    public EmployeePosition getEmpPosition() {
        return empPosition;
    }

    public void setEmpPosition(EmployeePosition empPosition) {
        this.empPosition = empPosition;
    }

    public EmployeeDetails getEmpImmediateSupervisor() {
        return empImmediateSupervisor;
    }

    public void setEmpImmediateSupervisor(EmployeeDetails empImmediateSupervisor) {
        this.empImmediateSupervisor = empImmediateSupervisor;
    }

    public double getEmpBasicSalary() {
        return empBasicSalary;
    }

    public void setEmpBasicSalary(double empSalary) {
        this.empBasicSalary = empSalary;
    }

    public double getEmpRice() {
        return empRice;
    }

    public void setEmpRice(double empRice) {
        this.empRice = empRice;
    }

    public double getEmpPhone() {
        return empPhone;
    }

    public void setEmpPhone(double empPhone) {
        this.empPhone = empPhone;
    }

    public double getEmpClothing() {
        return empClothing;
    }

    public void setEmpClothing(double empClothing) {
        this.empClothing = empClothing;
    }

    public double getEmpMonthlyRate() {
        return empMonthlyRate;
    }

    public void setEmpMonthlyRate(double empMonthlyRate) {
        this.empMonthlyRate = empMonthlyRate;
    }

    public double getEmpHourlyRate() {
        return empHourlyRate;
    }

    public void setEmpHourlyRate(double empHourlyRate) {
        this.empHourlyRate = empHourlyRate;
    }
    
    public String getFormattedName(){
        return (firstName+" "+lastName).trim();
    }
    
    public String getFormattedBirthday(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return this.empBirthday != null ? formatter.format(this.empBirthday) : null;
    }
   /* @Override 
    public String toString(){
        return "Employee ID: " + getEmpID()+"\n"+ "Employee Name: " + getFirstName()+" "+ getLastName()+"\n" +"Birthday: "+ getEmpBirthday();
    }*/
     
}
