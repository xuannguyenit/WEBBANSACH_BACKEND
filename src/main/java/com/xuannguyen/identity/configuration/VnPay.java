package com.xuannguyen.identity.configuration;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

public class VnPay {
    /**
     * URL thanh toán sandbox của VNPAY.
     */
    public static final String VNP_PAY_URL =
            "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    /**
     * URL API transaction query/refund của VNPAY.
     */
    public static final String VNP_API_URL =
            "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    /**
     * Mã website/terminal do VNPAY cấp.
     *
     * Nên đưa sang application.yml hoặc biến môi trường ở môi trường thật.
     */
    public static final String VNP_TMN_CODE = "J3JO6856";

    /**
     * Secret key do VNPAY cấp.
     *
     * Không nên commit key thật lên git.
     */
    public static final String VNP_HASH_SECRET =
            "1AYE08U3VK3G6DI09WJ0BPR2M9SQB0ZH";

    public static final String VNP_VERSION = "2.1.0";

    public static final String VNP_COMMAND = "pay";

    public static final String VNP_CURR_CODE = "VND";

    public static final String VNP_LOCALE = "vn";

    public static final String VNP_ORDER_TYPE = "billpayment";

    private static final SecureRandom RANDOM = new SecureRandom();

    private VnPay() {
    }

    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                return "";
            }

            Mac hmac512 = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            hmac512.init(secretKey);

            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(result.length * 2);

            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (Exception exception) {
            return "";
        }
    }

    public static String getRandomNumber(int length) {
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }

        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress != null && !ipAddress.isBlank()) {
            return ipAddress.split(",")[0].trim();
        }

        ipAddress = request.getHeader("X-Real-IP");

        if (ipAddress != null && !ipAddress.isBlank()) {
            return ipAddress.trim();
        }

        return request.getRemoteAddr();
    }

    public static String formatVnPayDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
        return formatter.format(date);
    }

    public static String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);

            if (fieldValue != null && !fieldValue.isBlank()) {
                if (!hashData.isEmpty()) {
                    hashData.append("&");
                }

                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(urlEncode(fieldValue));
            }
        }

        return hashData.toString();
    }

    public static String buildQueryString(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);

            if (fieldValue != null && !fieldValue.isBlank()) {
                if (!query.isEmpty()) {
                    query.append("&");
                }

                query.append(urlEncode(fieldName));
                query.append("=");
                query.append(urlEncode(fieldValue));
            }
        }

        return query.toString();
    }

    public static String createSecureHash(Map<String, String> params) {
        String hashData = buildHashData(params);
        return hmacSHA512(VNP_HASH_SECRET, hashData);
    }

    public static boolean verifySignature(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return false;
        }

        String receivedSecureHash = params.get("vnp_SecureHash");

        if (receivedSecureHash == null || receivedSecureHash.isBlank()) {
            return false;
        }

        Map<String, String> fields = new HashMap<>(params);

        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String calculatedSecureHash = createSecureHash(fields);

        return receivedSecureHash.equalsIgnoreCase(calculatedSecureHash);
    }

    public static String buildPaymentUrl(Map<String, String> params) {
        String secureHash = createSecureHash(params);

        String queryString = buildQueryString(params);

        return VNP_PAY_URL + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }
}

