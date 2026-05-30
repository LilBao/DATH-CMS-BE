package com.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WS, client kết nối tới /ws
        // withSockJS() cho phép fallback khi browser không hỗ trợ WS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "http://localhost:3000",
                        "http://localhost:3001"
                )
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix cho các message từ client gửi lên server
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix cho các topic mà client subscribe
        registry.enableSimpleBroker("/topic");
    }
}
