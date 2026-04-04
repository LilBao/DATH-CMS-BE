package com.cms.service.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SSEServiceImpl implements SSEService{
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subPayment(String orderId) {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(orderId, emitter);

        emitter.onCompletion(() -> emitters.remove(orderId));
        emitter.onTimeout(() -> emitters.remove(orderId));
        emitter.onError((e) -> emitters.remove(orderId));

        return emitter;
    }
    @Override
    public void sendEventPayment(String orderId, String event, Object data) {
        SseEmitter emitter = emitters.get(orderId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(event)
                        .data(data));
            } catch (Exception e) {
                emitters.remove(orderId);
            }
        }
    }
}
