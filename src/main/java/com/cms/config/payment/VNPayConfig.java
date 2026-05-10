package com.cms.config.payment;

import com.cms.util.VNPayUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@Getter
public class VNPayConfig {
    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.ipn-url}")
    private String vnpIpnUrl;

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.secret-key}")
    private String secretKey;

    @Value("${vnpay.version}")
    private String vnpVersion;

    @Value("${vnpay.command}")
    private String vnpCommand;

    @Value("${vnpay.order-type}")
    private String orderType;

    @Value("${vnpay.vnp-api-url}")
    private String vnpApiUrl;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();

        //set transaction info
        vnpParamsMap.put("vnp_Version", this.vnpVersion);
        vnpParamsMap.put("vnp_Command", this.vnpCommand);
        vnpParamsMap.put("vnp_TmnCode", this.vnpTmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnpReturnUrl);

        //set transaction time
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }

}