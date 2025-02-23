/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

/**
 *
 * @author leniejoice 
 */
public class EmployeeHours {


    private int empID;
    private Date date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private int id;
    

    public int getEmpID() {
        return empID;
    }

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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getFormattedHoursWorked(){
        long secondsDuration = getHoursWorked();
        return String.format("%d:%02d", secondsDuration / 3600, (secondsDuration % 3600) / 60);
    }
    
    public long getHoursWorked(){
        return (Duration.between(timeIn, timeOut).minus(timeIn.equals(LocalTime.MIDNIGHT) ? Duration.ZERO: Duration.ofHours(1))).toSeconds();
    }
    
}

