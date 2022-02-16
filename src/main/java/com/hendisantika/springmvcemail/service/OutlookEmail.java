package com.hendisantika.springmvcemail.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.sql.DatabaseMetaData;
import java.util.Date;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class OutlookEmail {


    public static void main(String[] args) {
        new OutlookEmail().sendEmail();
    }

    public void sendEmail() {
        final String username = "druida1036@hotmail.com";  // like yourname@outlook.com
        final String password = "7614175";   // password here

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        session.setDebug(true);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("jmartinez01581@gmail.com"));   // like inzi769@gmail.com
            message.setSubject("OutlookEmail");
            message.setText("HI you have done sending mail with outlook " + new Date());

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
