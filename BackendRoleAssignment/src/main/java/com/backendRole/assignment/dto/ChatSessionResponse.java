package com.backendRole.assignment.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data transfer object representing a chat session's details.
 */
@Data
public class ChatSessionResponse {
    /**
     * Unique identifier for the session.
     */
    private UUID id;

    /**
     * The title or topic of the session.
     */
    private String title;

    /**
     * The identifier of the user who owns this session.
     */
    private String userId;

    /**
     * Flag indicating if the session is marked as a favorite.
     */
    private boolean isFavorite;

    /**
     * The timestamp when the session was created.
     */
    private LocalDateTime createdAt;
}
