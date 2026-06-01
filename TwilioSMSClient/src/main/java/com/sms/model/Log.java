/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.model;
import java.sql.Timestamp;

/**
 *
 * @author mohesham
 */


public class Log {
    private int logId;
    private String rqstIp;
    private String logBody;
    private Timestamp logStamp;

    public Log() 
    {
    
    }

    public int getLogId()     
    { 
        return logId; 
    }
    public void setLogId(int id)      
    { 
        this.logId = id; 
    }

    
    public String getRqstIp()      
    { 
        return rqstIp; 
    }
    public void setRqstIp(String ip) 
    { 
        this.rqstIp = ip; 
    }

    
    public String getLogBody()         
    { 
        return logBody;
    }
    public void setLogBody(String b)    
    { 
        this.logBody = b; 
    }

    
    public Timestamp getLogStamp() 
    { 
        return logStamp; 
    }
    public void setLogStamp(Timestamp ts) 
    { 
        this.logStamp = ts; 
    }
}
