/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.model;

/**
 *
 * @author mohesham
 */

public class Admin {
    private int adminId;
    private String name;
    private String email;
    private String passwdHash;
    private boolean isSuper;

    public Admin() {}

    public int getAdminId()         
    { 
        return adminId; 
    }
    
    public void setAdminId(int id)  
    { 
        this.adminId = id; 
    }

    public String getName()            
    { 
        return name; 
    }
    
    public void setName(String name)   
    { 
        this.name = name; 
    }

    public String getEmail()             
    { 
        return email; 
    }
    
    public void setEmail(String email)   
    { 
        this.email = email; 
    }

    public String getPasswdHash()
    { 
        return passwdHash; 
    }
    
    public void setPasswdHash(String passwdHash) 
    { 
        this.passwdHash = passwdHash; 
    }

    public boolean isSuper()            
    { 
        return isSuper; 
    }
    public void setSuper(boolean s)      
    { 
        this.isSuper = s; 
    }
    
}