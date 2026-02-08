package com.backendRole.assignment.dto;

import com.backendRole.assignment.model.ChatMessage;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data transfer object for message details.
 * Used when retrieving message history.
 */
@Data
public class ChatMessageResponse {
    /**
     * Unique identifier for the message.
     */
    private UUID id;

    /**
     * The role of the sender (USER or AI).
     */
    private ChatMessage.Sender role;

    /**
     * The text content of the message.
     */
    private String content;

    /**
     * Optional grounding context or metadata for the message.
     */
    private String context;

    /**
     * The timestamp indicating when the message was created.
     */
    private LocalDateTime timestamp;

    /**
     * The ID of the session this message belongs to.
     */
    private UUID sessionId;
}
