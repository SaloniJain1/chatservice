package com.backendRole.assignment.dto;

import lombok.Data;

/**
 * Data transfer object for updating an existing chat session.
 */
@Data
public class UpdateSessionRequest {
    /**
     * The new title for the session.
     */
    private String title;

    /**
     * Flag to mark or unmark the session as a favorite.
     */
    private Boolean isFavorite;
}
