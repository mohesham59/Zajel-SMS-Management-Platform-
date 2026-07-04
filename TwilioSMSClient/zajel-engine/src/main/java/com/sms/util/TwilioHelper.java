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

    public static void sendVerificationCode(String msisdn, String code) throws Exception {
        try (java.io.InputStream input = TwilioHelper.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find config.properties in classpath.");
            }
            java.util.Properties props = new java.util.Properties();
            props.load(input);

            String accountSid = props.getProperty("twilio.sid") != null ? props.getProperty("twilio.sid").trim().replaceAll("^\"|\"$", "") : null;
            String authToken = props.getProperty("twilio.token") != null ? props.getProperty("twilio.token").trim().replaceAll("^\"|\"$", "") : null;
            String twilioNumber = props.getProperty("twilio.number") != null ? props.getProperty("twilio.number").trim().replaceAll("^\"|\"$", "") : null;

            if (accountSid == null || authToken == null || twilioNumber == null) {
                throw new IllegalStateException("One or more Twilio keys are missing from config.properties!");
            }

            sendSms(accountSid, authToken, twilioNumber, msisdn, "Zajel: Your verification code is: " + code);
        }
    }
}