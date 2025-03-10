/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;

/**
 *
 * @author leniejoice 
 */
public class Employee extends Person{


    private Date date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private int attendanceId;
    
    
    public Employee() {
        super(0, "", "", "", null, "", "", "", 0, 0, null, null, null,0,0,0,0,0,0); 
        
    }
    public Employee(int empID, String lastName, String firstName, String empAddress, Date empBirthday,
                    String empPhoneNumber, String empSSS, String empTIN, long empPhilHealth,
                    long empPagibig, Person empImmediateSupervisor, EmployeeStatus empStatus,
                    EmployeePosition empPosition,double empBasicSalary, double empRice,
                   double empPhone, double empClothing, double empMonthlyRate, double empHourlyRate, Date date, LocalTime timeIn, LocalTime timeOut, int attendanceId) {
        
        super(empID, lastName, firstName, empAddress, empBirthday, empPhoneNumber, empSSS, 
              empTIN, empPhilHealth, empPagibig, empImmediateSupervisor, empStatus, empPosition,
              empBasicSalary,empRice,empPhone,empClothing, empMonthlyRate,empHourlyRate);
        
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.attendanceId = attendanceId;
    }
    

    @Override
    public int getEmpID() {
        return empID;
    }

    @Override
    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }


    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }
    
    public String getFormattedHoursWorked(){
        long secondsDuration = getHoursWorked();
        return String.format("%d:%02d", secondsDuration / 3600, (secondsDuration % 3600) / 60);
    }
    
    public long getHoursWorked(){
        return (Duration.between(timeIn, timeOut).minus(timeIn.equals(LocalTime.MIDNIGHT) ? Duration.ZERO: Duration.ofHours(1))).toSeconds();
    }

    
}

