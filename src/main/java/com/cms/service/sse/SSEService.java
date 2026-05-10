package com.cms.service.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SSEService {
    SseEmitter subPayment(String orderId);
    void sendEventPayment(String orderId, String event, Object data);
}
