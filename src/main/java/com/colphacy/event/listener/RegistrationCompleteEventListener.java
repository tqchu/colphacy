package com.colphacy.event.listener;

import com.colphacy.event.RegistrationCompleteEvent;
import com.colphacy.model.Customer;
import com.colphacy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private final CustomerService customerService;

    @Autowired
    private final JavaMailSender mailSender;
    private Customer customer;

    @Value("${client-web-url}")
    private String clientWebUrl;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        customer = event.getCustomer();
        String verificationToken = UUID.randomUUID().toString();
        customerService.saveCustomerVerificationToken(customer, verificationToken);
        String url = clientWebUrl + "verify-email/" + verificationToken;
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link to verify your registration :  {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Xác nhận đăng ký";
        String senderName = "Hệ thống nhà thuốc Colphacy";
        String mailContent = "<p> Xin chào <b>" + customer.getFullName() + "</b>, </p>" +
                "<p>Cảm ơn anh/chị đã đăng ký với <b>Colphacy</b> <br>" + "" +
                "Vui lòng nhấn vào link dưới đây để hoàn tất đăng ký</p>" +
                "<a href=\"" + url + "\">Xác nhận đăng ký</a>" +
                "<p> Cảm ơn !!! <br> Hệ thống nhà thuốc Colphacy";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("colphacy@gmail.com", senderName);
        messageHelper.setTo(customer.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}