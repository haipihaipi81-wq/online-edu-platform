package com.edu.module.live.websocket;

import com.edu.module.live.entity.LiveChat;
import com.edu.module.live.service.LiveService;
import com.edu.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final LiveService liveService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // roomId -> (sessionId -> session)
    private static final Map<Long, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long roomId = extractRoomId(session);
        String token = extractToken(session);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        Long userId = jwtTokenProvider.getUserId(token);
        session.getAttributes().put("userId", userId);
        session.getAttributes().put("roomId", roomId);

        rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        log.info("用户 {} 进入直播间 {}", userId, roomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long roomId = (Long) session.getAttributes().get("roomId");
        Long userId = (Long) session.getAttributes().get("userId");

        Map<?, ?> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String content = (String) payload.get("content");

        LiveChat chat = liveService.saveChat(roomId, userId, content);

        // Broadcast to all in room
        String json = objectMapper.writeValueAsString(Map.of(
                "type", "chat",
                "id", chat.getId(),
                "userId", chat.getUserId(),
                "username", chat.getUsername(),
                "avatar", chat.getAvatar(),
                "content", chat.getContent(),
                "createdAt", chat.getCreatedAt() != null ? chat.getCreatedAt().toString() : null
        ));

        TextMessage broadcast = new TextMessage(json);
        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            for (WebSocketSession ws : roomSessions.values()) {
                if (ws.isOpen()) {
                    try {
                        ws.sendMessage(broadcast);
                    } catch (IOException e) {
                        log.error("发送消息失败", e);
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long roomId = (Long) session.getAttributes().get("roomId");
        if (roomId != null) {
            Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
            if (roomSessions != null) {
                roomSessions.remove(session.getId());
                if (roomSessions.isEmpty()) {
                    rooms.remove(roomId);
                }
            }
        }
    }

    private Long extractRoomId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String path = uri.getPath();
        String[] parts = path.split("/");
        try {
            return Long.parseLong(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractToken(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query != null && query.startsWith("token=")) {
            return query.substring(6);
        }
        return null;
    }
}
