/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.model;

/**
 *
 * @author mohesham
 */

public class ErrCod {
    private int errorId;
    private String errorMsg;
    private String description;

    public ErrCod() 
    {
    
    }

    public int getErrorId() 
    { 
        return errorId; 
    }
    public void setErrorId(int id)   
    { 
        this.errorId = id; 
    }

    
    public String getErrorMsg()       
    { 
        return errorMsg; 
    }
    public void setErrorMsg(String m) 
    { 
        this.errorMsg = m; 
    }

    
    public String getDescription()  
    { 
        return description; 
    }
    public void setDescription(String d) 
    { 
        this.description = d; 
    }
}