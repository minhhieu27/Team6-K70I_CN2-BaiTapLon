package com.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.app.security.JwtHandleshakeInterceptor;
import com.app.socket.AuctionWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final AuctionWebSocketHandler auctionWebSocketHandler;
    private final JwtHandleshakeInterceptor jwtHandleshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(auctionWebSocketHandler, "/ws/auction").addInterceptors(jwtHandleshakeInterceptor).setAllowedOrigins("*");
    }
}
