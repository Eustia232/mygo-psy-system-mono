package com.mygo;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest(args = "--mpw.key=fqOS7bGCn3sxsTIL")
public class MailTest {


    @Autowired
    private JavaMailSender mailSender;


    @Test
    public void mailTest() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("0721");
        message.setText("0722");
        message.setFrom("a728076618@163.com");
        message.setTo("a728076618@163.com");
        mailSender.send(message);
    }
}
