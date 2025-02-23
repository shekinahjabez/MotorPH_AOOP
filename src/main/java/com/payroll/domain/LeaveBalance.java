/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

import com.payroll.domain.LeaveDetails.LeaveStatus;
import java.util.List;

/**
 *
 * @author leniejoice
 */
public class LeaveBalance {
    
   private int empID;
   private int total = 25;
   private int available = 25;
   private int taken = 0;

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int taken) {
        this.taken = taken;
    }
    
    public void updateLeaveBalance(List<LeaveDetails> leaveDetails){
        int totalDays = 0;
        for(LeaveDetails l: leaveDetails){
            if(!l.getStatus().equals(LeaveStatus.DECLINED.name())){
                totalDays += l.getTotalDays();
            }
        }
        setTaken(totalDays);
        int available = total - taken;
        setAvailable(available);
    }
   
            
    
}
