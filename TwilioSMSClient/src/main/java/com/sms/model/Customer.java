/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.model;
import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author mohesham
 */



public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String passwd;
    private String msisdn;
    private Date birthday;
    private String job;
    private CustomerAddress customerAddress;
    private String sid;      // Twilio Account SID
    private String token;    // Twilio Auth Token
    private Timestamp createdAt;

    // Verification fields (not in DB – stored in session)
    private String verificationCode;
    private boolean verified;

    public Customer() 
    {
    
    }

    
    public int getCustomerId()           
    { 
        return customerId; 
    }
    public void setCustomerId(int id)    
    { 
        this.customerId = id; 
    }

    
    public String getName()              
    { 
        return name; 
    }
    public void setName(String n)        
    { 
        this.name = n; 
    }

    
    public String getEmail()             
    { 
        return email; 
    }
    public void setEmail(String e)     
    { 
        this.email = e;
    }

    
    public String getPasswd()            
    { 
        return passwd; 
    }
    public void setPasswd(String p)     
    { 
        this.passwd = p; 
    }

    
    public String getMsisdn()            
    { 
        return msisdn;
    }
    public void setMsisdn(String m)   
    { 
        this.msisdn = m; 
    }

    
    public Date getBirthday()            
    { 
        return birthday; 
    }
    public void setBirthday(Date b) 
    { 
        this.birthday = b; 
    }

    
    public String getJob()               
    { 
        return job; 
    }
    public void setJob(String j)     
    { 
        this.job = j; 
    }

    
    public CustomerAddress getCustomerAddress()
    { 
        return customerAddress; 
    }
    public void setCustomerAddress(CustomerAddress addr)  
    { 
        this.customerAddress = addr; 
    }

    
    public String getSid()        
    { 
        return sid; 
    }
    public void setSid(String s)    
    { 
        this.sid = s; 
    }
    

    public String getToken()             
    { 
        return token; 
    }
    public void setToken(String t)   
    { 
        this.token = t; 
    }

    
    public Timestamp getCreatedAt()    
    { 
        return createdAt; 
    }
    public void setCreatedAt(Timestamp ts) 
    { 
        this.createdAt = ts; 
    }

    
    public String getVerificationCode()  
    { 
        return verificationCode;
    }
    public void setVerificationCode(String code)
    { 
        this.verificationCode = code; 
    }

    
    public boolean isVerified()         
    { 
        return verified; 
    }
    public void setVerified(boolean v) 
    { 
        this.verified = v; 
    }
}
