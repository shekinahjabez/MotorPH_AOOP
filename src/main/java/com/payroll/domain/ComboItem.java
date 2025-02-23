/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

/**
 *
 * @author leniejoice
 */
public class ComboItem {
    private Integer key;
    private String value;

    public ComboItem(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;  // This will be the text displayed in the combo box
    }

    public String getValue() {
        return value;  // This will allow you to retrieve the stored ID
    }
    
    public Integer getKey(){
        return key;
    }
    
    public boolean equals(Object other) {
       if (other == null) { 
            return false;	
        }

       if (!(other instanceof ComboItem)) {
           return false;	
        } 

       ComboItem item = (ComboItem) other; 
       return this.getValue().equals(item.getValue());
    }
}
