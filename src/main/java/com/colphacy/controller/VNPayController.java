package com.colphacy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class VNPayController {
    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    @GetMapping("/return")
    public RedirectView returnUrl() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://colphacy-user-client.vercel.app/personal/my-order");
        return redirectView;
    }

    @PostMapping
    public String getPaymentUrl(HttpServletRequest request) throws UnsupportedEncodingException {
        // TODO: make as config
        String vnp_Version = "2.1.0";
        // TODO: make as config
        String vnp_Command = "pay";
        // TODO:
        String vnp_OrderInfo = "Thanh toán đơn hàng";
        // TODO:
        String orderType = "other";

        Random random = new Random();
        String vnp_TxnRef = String.valueOf(random.nextInt(6));
        String vnp_IpAddr = request.getRemoteAddr();
        ;
        String vnp_TmnCode = "W2UVZPQJ";

        int amount = 120000 * 100;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
//        String bank_code = request.getParameter("bankcode");
//        if (bank_code != null && !bank_code.isEmpty()) {
//            vnp_Params.put("vnp_BankCode", bank_code);
//        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = request.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", "https://colphacy.tech/api/payment/return");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

//        //Billing
//        vnp_Params.put("vnp_Bill_Mobile", request.getParameter("txt_billing_mobile"));
//        vnp_Params.put("vnp_Bill_Email", request.getParameter("txt_billing_email"));
//        String fullName = (request.getParameter("txt_billing_fullname")).trim();
//        if (fullName != null && !fullName.isEmpty()) {
//            int idx = fullName.indexOf(' ');
//            String firstName = fullName.substring(0, idx);
//            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
//            vnp_Params.put("vnp_Bill_FirstName", firstName);
//            vnp_Params.put("vnp_Bill_LastName", lastName);
//        }
//        vnp_Params.put("vnp_Bill_Address", request.getParameter("txt_inv_addr1"));
//        vnp_Params.put("vnp_Bill_City", request.getParameter("txt_bill_city"));
//        vnp_Params.put("vnp_Bill_Country", request.getParameter("txt_bill_country"));
//        if (request.getParameter("txt_bill_state") != null && !request.getParameter("txt_bill_state").isEmpty()) {
//            vnp_Params.put("vnp_Bill_State", request.getParameter("txt_bill_state"));
//        }
//
//        // Invoice
//        vnp_Params.put("vnp_Inv_Phone", request.getParameter("txt_inv_mobile"));
//        vnp_Params.put("vnp_Inv_Email", request.getParameter("txt_inv_email"));
//        vnp_Params.put("vnp_Inv_Customer", request.getParameter("txt_inv_customer"));
//        vnp_Params.put("vnp_Inv_Address", request.getParameter("txt_inv_addr1"));
//        vnp_Params.put("vnp_Inv_Company", request.getParameter("txt_inv_company"));
//        vnp_Params.put("vnp_Inv_Taxcode", request.getParameter("txt_inv_taxcode"));
//        vnp_Params.put("vnp_Inv_Type", request.getParameter("cbo_inv_type"));

        //Build data to hash and querystring
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        //...
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512("TKUGPPKENCQZIYNOJSJEJJYFQZEJSGEZ", hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html" + "?" + queryUrl;
        System.out.println(vnp_SecureHash);
        return paymentUrl;
    }
//    @PostMapping("/return")
//    public String returnUrl(HttpServletRequest request) throws UnsupportedEncodingException {
//        Map fields = new HashMap();
//        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
//            String fieldName = (String) params.nextElement();
//            String fieldValue = request.getParameter(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                fields.put(fieldName, fieldValue);
//            }
//        }
//
//        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//        if (fields.containsKey("vnp_SecureHashType")) {
//            fields.remove("vnp_SecureHashType");
//        }
//        if (fields.containsKey("vnp_SecureHash")) {
//            fields.remove("vnp_SecureHash");
//        }
//        String signValue = Config.hashAllFields(fields);
//
//        if (signValue.equals(vnp_SecureHash)) {
//            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
//                out.print("GD Thanh cong");
//            } else {
//                out.print("GD Khong thanh cong");
//            }
//
//        } else {
//            out.print("Chu ky khong hop le");
//        }
//        return paymentUrl;
//    }

}
