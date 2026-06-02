package com.app.security;

import com.app.entity.user.UserEntity;
import com.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandleshakeInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {
        try {
            String query = request.getURI().getQuery();

            if (query == null || !query.startsWith("token=")) {
                return false;
            }

            String token = query.substring("token=".length());
            String userId = jwtUtil.extractUsername(token);

            if (userId == null) {
                return false;
            }

            UserEntity user = userRepository.findByUserId(userId).orElse(null);

            if (user == null) {
                return false;
            }

            attributes.put("userId", user.getUserId());
            attributes.put("username", user.getUsername());
            attributes.put("role", user.getRoles());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}