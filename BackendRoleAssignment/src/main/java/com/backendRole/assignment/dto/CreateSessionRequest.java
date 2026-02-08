package com.backendRole.assignment.dto;

import lombok.Data;

/**
 * Data transfer object for starting a new chat session.
 */
@Data
public class CreateSessionRequest {
    /**
     * The initial title or topic of the chat session.
     */
    private String title;
}
