package com.colphacy.event.listener;

import com.colphacy.event.ChangeOrderStatusEvent;
import com.colphacy.model.Customer;
import com.colphacy.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeOrderStatusEventListener implements ApplicationListener<ChangeOrderStatusEvent> {
    @Autowired
    private final JavaMailSender mailSender;
    private Customer customer;
    private Long orderId;
    private OrderStatus status;

    @Override
    public void onApplicationEvent(ChangeOrderStatusEvent event) {
        customer = event.getCustomer();
        orderId = event.getOrderId();
        status = event.getOrderStatus();
        try {
            sendUpdateOrderStatusEmail(customer.getFullName(), customer.getEmail(), orderId, status);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendUpdateOrderStatusEmail(String customerFullName, String customerEmail, Long orderId, OrderStatus status) throws MessagingException, UnsupportedEncodingException {
        CompletableFuture.runAsync(() -> {
            try {
                String subject = subjectEmail(orderId, status);
                String senderName = "Hệ thống nhà thuốc Colphacy";
                String detailOrderUrl = "https://colphacy-user-client.vercel.app/personal/my-order/" + orderId;
                String mailContent = "<p> Xin chào <b>" + customerFullName + "</b>, </p>" +
                        "<p>Cảm ơn anh/chị đã mua hàng tại <b>Colphacy</b> <br>" +
                        emailContent(orderId, status) +
                        "<a href=\"" + detailOrderUrl + "\">Xem chi tiết đơn hàng</a>" +
                        "<p> Cảm ơn bạn đã mua hàng tại Colphacy !!! <br> Hệ thống cửa hàng thuốc Colphacy</p>";

                MimeMessage message = mailSender.createMimeMessage();
                var messageHelper = new MimeMessageHelper(message);
                messageHelper.setFrom("colphacy@gmail.com", senderName);
                messageHelper.setTo(customerEmail);
                messageHelper.setSubject(subject);
                messageHelper.setText(mailContent, true);
                mailSender.send(message);
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        });

    }


    private String subjectEmail(Long orderId, OrderStatus status) {
        switch (status) {
            case TO_PAY:
                return "Đơn hàng #" + orderId + " đang chờ thanh toán";
            case PENDING:
                return "Đơn hàng #" + orderId + " đang được chờ xác nhận";
            case CONFIRMED:
                return "Đơn hàng #" + orderId + " đã được xác nhận";
            case SHIPPING:
                return "Đơn hàng #" + orderId + " đang trên đường giao đến bạn";
            case DELIVERED:
                return "Đơn hàng #" + orderId + " đã giao hàng thành công";
            case CANCELLED:
                return "Đơn hàng #" + orderId + " đã được hủy";
            case RETURNED:
                return "Đơn hàng #" + orderId + " đã được yêu cầu hoàn trả";
            default:
                return null;
        }
    }

    private String emailContent(Long orderId, OrderStatus status) {
        switch (status) {
            case TO_PAY:
                return "Đơn hàng <b>#" + orderId + "</b> của bạn đã được đặt. Vui lòng thanh toán đơn hàng trong 3 giờ tới.</p>";
            case PENDING:
                return  "Đơn hàng <b>#" + orderId + "</b> của bạn đang được chờ xác nhận </p>";
            case CONFIRMED:
                return  "Đơn hàng <b>#" + orderId + "</b> của bạn đã được xác nhận </p>";
            case SHIPPING:
                return  "Đơn hàng <b>#" + orderId + "</b> của bạn đang được giao</p>";
            case DELIVERED:
                return  "Đơn hàng <b>#" + orderId + "</b> của bạn đã được giao thành công</p>";
            case CANCELLED:
                return  "Đơn hàng <b>#" + orderId + "</b> của bạn đã được hủy thành công</p>";
            case RETURNED:
                return "Đơn hàng #" + orderId + " đã được yêu cầu hoàn trả";
            default:
                return null;
        }
    }
}
