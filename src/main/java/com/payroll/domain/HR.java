/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;
import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;
import com.payroll.subdomain.LeaveType;

import java.time.LocalTime;
import java.util.Date;

/**
 *
 * @author leniejoice
 */
public class HR extends Person {

    
    public static enum LeaveStatus{
        PENDING,APPROVED,DECLINED
    }
    
    private int leaveId;
    private String subject;
    private LeaveType leaveType;
    private Date dateFrom;
    private Date dateTo;
    private int totalDays;
    private String reason;
    private LeaveStatus status;
    
    
    public HR() {
        super(0, "", "", "", null, "", "", "", 0, 0, null, null, null,0,0,0,0,0,0); 
    }
    
     public HR(int empID, String lastName, String firstName, String empAddress, Date empBirthday,
              String empPhoneNumber, String empSSS, String empTIN, long empPhilHealth,
              long empPagibig, Person empImmediateSupervisor, EmployeeStatus empStatus,
              EmployeePosition empPosition,double empBasicSalary, double empRice,
                   double empPhone, double empClothing, double empMonthlyRate, double empHourlyRate, int leaveId, String subject, LeaveType leaveType,
              Date dateFrom, Date dateTo, int totalDays, String reason, LeaveStatus status) {

        super(empID, lastName, firstName, empAddress, empBirthday, empPhoneNumber, empSSS, 
              empTIN, empPhilHealth, empPagibig, empImmediateSupervisor, empStatus, empPosition,
              empBasicSalary,empRice,empPhone,empClothing, empMonthlyRate,empHourlyRate);
        
        this.leaveId = leaveId;
        this.subject = subject;
        this.leaveType = leaveType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.totalDays = totalDays;
        this.reason = reason;
        this.status = status;
    }

    @Override
    public int getEmpID() {
        return empID;
    }

    @Override
    public void setEmpID(int empID) {
        this.empID = empID;
    }
    
    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
    
    
}
