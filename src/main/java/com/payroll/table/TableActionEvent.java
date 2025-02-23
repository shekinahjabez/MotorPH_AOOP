/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.table;

/**
 *
 * @author leniejoice
 */
public interface TableActionEvent {
    
    public void onApprove(int row);
    public void onDecline(int row);
    
}
