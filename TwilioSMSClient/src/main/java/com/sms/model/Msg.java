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
public class Msg {

    private int msgId;
    private int senderId;
    private String receiverMsisdn;
    private String msgBody;
    private Timestamp msgStamp;
    private Integer errorCode;  // nullable

    // Joined fields for display
    private String errorMsg;
    private String senderName;
    private String status; // "SUCCESS" or "FAILED"

    public Msg() {

    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int id) {
        this.msgId = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int id) {
        this.senderId = id;
    }

    public String getReceiverMsisdn() {
        return receiverMsisdn;
    }

    public void setReceiverMsisdn(String r) {
        this.receiverMsisdn = r;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String b) {
        this.msgBody = b;
    }

    public Timestamp getMsgStamp() {
        return msgStamp;
    }

    public void setMsgStamp(Timestamp ts) {
        this.msgStamp = ts;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer ec) {
        this.errorCode = ec;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String em) {
        this.errorMsg = em;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String sn) {
        this.senderName = sn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = s;
    }
}
