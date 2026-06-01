/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.util;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 *
 * @author mohesham
 */

public class TwilioHelper {
    
    public static String sendSms(String accountSid, String authToken,
                                  String from, String to, String body) {
        Twilio.init(accountSid, authToken);
        Message msg = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                body
        ).create();
        return msg.getSid();
    }

    public static String generateCode() {
        int code = 100000 + (int)(Math.random() * 900000);
        return String.valueOf(code);
    }
}