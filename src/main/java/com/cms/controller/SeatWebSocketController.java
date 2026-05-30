package com.cms.controller;

import com.cms.dto.websocket.SeatLockMessage;
import com.cms.dto.websocket.SeatStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Xử lý WebSocket: lock/unlock ghế theo suất chiếu.
 *
 * In-memory state (đơn giản, không persist):
 *   lockedSeats: timeId → Set<"sRow-sColumn">
 *   sessionSeatMap: sessionId → Set<SeatStatusMessage> (để tự unlock khi disconnect)
 */
@Slf4j
@Controller
public class SeatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    // timeId → Set của "sRow-sCol" đang bị lock
    private final Map<Integer, Set<String>> lockedSeats = new ConcurrentHashMap<>();

    // sessionId → danh sách ghế đang lock (để cleanup khi disconnect)
    private final Map<String, Set<SeatStatusMessage>> sessionSeatMap = new ConcurrentHashMap<>();

    public SeatWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Client gửi tới /app/seats/lock với payload SeatLockMessage.
     * Header "simpSessionId" dùng để track disconnect.
     */
    @MessageMapping("/seats/lock")
    public void handleSeatLock(SeatLockMessage msg, StompHeaderAccessor accessor) {
        if (msg.getTimeId() == null || msg.getSRow() == null || msg.getSColumn() == null) {
            return;
        }

        String seatKey = msg.getSRow() + "-" + msg.getSColumn();
        String sessionId = accessor.getSessionId();
        String status;

        if ("LOCK".equalsIgnoreCase(msg.getAction())) {
            // Thêm vào tập lock của suất chiếu
            lockedSeats
                    .computeIfAbsent(msg.getTimeId(), k -> ConcurrentHashMap.newKeySet())
                    .add(seatKey);

            // Lưu vào map session để cleanup sau
            SeatStatusMessage statusMsg = new SeatStatusMessage(msg.getTimeId(), msg.getSRow(), msg.getSColumn(), "LOCKED");
            sessionSeatMap
                    .computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                    .add(statusMsg);

            status = "LOCKED";
            log.debug("LOCK seat {}/{} for timeId={} session={}", msg.getSRow(), msg.getSColumn(), msg.getTimeId(), sessionId);

        } else {
            // UNLOCK
            Set<String> seats = lockedSeats.get(msg.getTimeId());
            if (seats != null) seats.remove(seatKey);

            // Xóa khỏi session map
            Set<SeatStatusMessage> sessionSeats = sessionSeatMap.get(sessionId);
            if (sessionSeats != null) {
                sessionSeats.removeIf(s -> s.getSRow().equals(msg.getSRow()) && s.getSColumn().equals(msg.getSColumn()));
            }

            status = "UNLOCKED";
            log.debug("UNLOCK seat {}/{} for timeId={} session={}", msg.getSRow(), msg.getSColumn(), msg.getTimeId(), sessionId);
        }

        // Broadcast tới tất cả client đang xem cùng suất chiếu
        SeatStatusMessage broadcast = new SeatStatusMessage(msg.getTimeId(), msg.getSRow(), msg.getSColumn(), status);
        messagingTemplate.convertAndSend("/topic/seats/" + msg.getTimeId(), broadcast);
    }

    /**
     * Khi client disconnect (đóng tab, mất mạng...), tự động unlock tất cả ghế của session đó.
     */
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        Set<SeatStatusMessage> seats = sessionSeatMap.remove(sessionId);
        if (seats == null || seats.isEmpty()) return;

        log.debug("Session {} disconnected, unlocking {} seats", sessionId, seats.size());

        for (SeatStatusMessage seat : seats) {
            // Xóa khỏi locked set
            Set<String> locked = lockedSeats.get(seat.getTimeId());
            if (locked != null) {
                locked.remove(seat.getSRow() + "-" + seat.getSColumn());
            }

            // Broadcast UNLOCKED
            SeatStatusMessage broadcast = new SeatStatusMessage(
                    seat.getTimeId(), seat.getSRow(), seat.getSColumn(), "UNLOCKED"
            );
            messagingTemplate.convertAndSend("/topic/seats/" + seat.getTimeId(), broadcast);
        }
    }
}
