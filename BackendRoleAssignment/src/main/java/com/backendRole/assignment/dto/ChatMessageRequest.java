package com.backendRole.assignment.dto;

import com.backendRole.assignment.model.ChatMessage;
import lombok.Data;

/**
 * Data transfer object for adding a new message to a session.
 * Contains the role of the sender and the message content.
 */
@Data
public class ChatMessageRequest {
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
}
