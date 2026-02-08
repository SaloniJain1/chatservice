package com.backendRole.assignment.controller;

import com.backendRole.assignment.dto.*;
import com.backendRole.assignment.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing chat sessions and messages.
 * Provides endpoints for creating, retrieving, updating, and deleting chat
 * history.
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Management", description = "Endpoints for managing chatbot history and sessions")
public class ChatController {

    private final ChatService chatService;

    /**
     * Creates a new chat session for a user.
     *
     * @param payload the request body containing session title
     * @param userId  the authenticated user ID from JWT
     * @return the created session details
     */
    @Operation(summary = "Create a new chat session", description = "Initializes a new chat history session for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ChatSessionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid or missing token", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ChatSessionResponse createSession(
            @RequestBody CreateSessionRequest payload,
            @Parameter(hidden = true) @RequestHeader("x-user-id") String userId) {

        log.info("API CALL: createSession | User: {} | Title: {}", userId, payload.getTitle());
        log.debug("Payload: {}", payload);

        try {
            ChatSessionResponse session = chatService.createSession(payload, userId);
            log.info("API SUCCESS: createSession | SessionID: {}", session.getId());
            return session;
        } catch (Exception e) {
            log.error("API ERROR: createSession | User: {} | Error: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all chat sessions for the authenticated user.
     *
     * @param userId the authenticated user ID from JWT
     * @return a list of session details
     */
    @Operation(summary = "Get all chat sessions", description = "Retrieves a list of all chat history sessions for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ChatSessionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid or missing token", content = @Content)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ChatSessionResponse> getAllSessions(
            @Parameter(hidden = true) @RequestHeader("x-user-id") String userId) {

        log.info("API CALL: getAllSessions | User: {}", userId);

        try {
            List<ChatSessionResponse> sessions = chatService.getAllSessions(userId);
            log.info("API SUCCESS: getAllSessions | User: {} | Count: {}", userId, sessions.size());
            return sessions;
        } catch (Exception e) {
            log.error("API ERROR: getAllSessions | User: {} | Error: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a specific chat session by ID.
     *
     * @param id     the UUID of the session
     * @param userId the authenticated user ID from JWT
     * @return the session details
     */
    @Operation(summary = "Get session by ID", description = "Fetches details of a specific chat session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ChatSessionResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChatSessionResponse getSession(
            @Parameter(description = "UUID of the session") @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("x-user-id") String userId) {

        log.info("API CALL: getSession | User: {} | SessionID: {}", userId, id);

        try {
            ChatSessionResponse session = chatService.getSession(id);
            log.info("API SUCCESS: getSession | SessionID: {}", id);
            return session;
        } catch (Exception e) {
            log.warn("API WARNING: getSession | Session Not Found: {} | Error: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Updates an existing chat session (e.g., changing the title).
     *
     * @param id      the UUID of the session
     * @param updates the partial updates for the session
     * @param userId  the authenticated user ID from JWT
     * @return the updated session details
     */
    @Operation(summary = "Update a chat session", description = "Updates metadata of an existing session, such as its title.")
    @ApiResponse(responseCode = "200", description = "Session updated successfully")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChatSessionResponse updateSession(
            @Parameter(description = "UUID of the session") @PathVariable UUID id,
            @RequestBody UpdateSessionRequest updates,
            @Parameter(hidden = true) @RequestHeader("x-user-id") String userId) {

        log.info("API CALL: updateSession | User: {} | SessionID: {}", userId, id);
        log.debug("Update Values: {}", updates);

        try {
            ChatSessionResponse session = chatService.updateSession(id, updates);
            log.info("API SUCCESS: updateSession | SessionID: {}", id);
            return session;
        } catch (Exception e) {
            log.error("API ERROR: updateSession | SessionID: {} | Error: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a chat session and its message history.
     *
     * @param id     the UUID of the session
     * @param userId the authenticated user ID from JWT
     */
    @Operation(summary = "Delete a chat session", description = "Permanently removes a session and all its associated messages.")
    @ApiResponse(responseCode = "204", description = "Session deleted successfully")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            @Parameter(description = "UUID of the session") @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("x-user-id") String userId) {

        log.info("API CALL: deleteSession | User: {} | SessionID: {}", userId, id);

        try {
            chatService.deleteSession(id);
            log.info("API SUCCESS: deleteSession | SessionID: {}", id);
        } catch (Exception e) {
            log.error("API ERROR: deleteSession | SessionID: {} | Error: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Adds a new message (User or AI) to a chat session.
     *
     * @param id      the UUID of the session
     * @param message the message content and role
     * @param userId  the authenticated user ID from JWT
     * @return the created message details
     */
    @Operation(summary = "Add message to session", description = "Appends a new USER or AI message to the specified chat session.")
    @ApiResponse(responseCode = "200", description = "Message added successfully")
    @PostMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.OK)
    public ChatMessageResponse addMessage(
            @Parameter(description = "UUID of the session") @PathVariable UUID id,
            @RequestBody ChatMessageRequest message,
            @Parameter(hidden = true) @RequestHeader("x-user-id") String userId) {

        log.info("API CALL: addMessage | User: {} | SessionID: {} | Role: {}", userId, id, message.getRole());
        log.debug("Message Content: {}", message.getContent());

        try {
            ChatMessageResponse created = chatService.addMessage(id, message);
            log.info("API SUCCESS: addMessage | MessageID: {}", created.getId());
            return created;
        } catch (Exception e) {
            log.error("API ERROR: addMessage | SessionID: {} | Error: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a paginated list of messages for a session.
     *
     * @param id       the UUID of the session
     * @param pageable pagination parameters (page, size, sort)
     * @param userId   the authenticated user ID from JWT
     * @return a page of message details
     */
    @Operation(summary = "Get messages for a session", description = "Fetches a paginated history of messages for the specified session.")
    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    @GetMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.OK)
    public Page<ChatMessageResponse> getMessages(
            @Parameter(description = "UUID of the session") @PathVariable UUID id,
            Pageable pageable,
            @Parameter(hidden = true) @RequestHeader("x-user-id") String userId) {

        log.info("API CALL: getMessages | User: {} | SessionID: {} | Page: {}", userId, id, pageable.getPageNumber());

        try {
            Page<ChatMessageResponse> messages = chatService.getMessages(id, pageable);
            log.info("API SUCCESS: getMessages | SessionID: {} | TotalElements: {}", id, messages.getTotalElements());
            return messages;
        } catch (Exception e) {
            log.error("API ERROR: getMessages | SessionID: {} | Error: {}", id, e.getMessage());
            throw e;
        }
    }
}
