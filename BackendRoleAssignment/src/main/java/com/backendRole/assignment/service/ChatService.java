package com.backendRole.assignment.service;

import com.backendRole.assignment.dto.*;
import com.backendRole.assignment.model.ChatMessage;
import com.backendRole.assignment.model.ChatSession;
import com.backendRole.assignment.repository.ChatMessageRepository;
import com.backendRole.assignment.repository.ChatSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing chat session and message operations.
 * Handles the business logic for creating sessions and maintaining message
 * history.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    /**
     * Creates a new chat session for a user.
     *
     * @param request the session creation details
     * @param userId  the owner of the session
     * @return the created session's data transfer object
     */
    public ChatSessionResponse createSession(CreateSessionRequest request, String userId) {
        log.info("Creating new chat session for user: {} with title: '{}'", userId, request.getTitle());

        ChatSession session = new ChatSession();
        session.setTitle(request.getTitle());
        session.setUserId(userId);
        // timestamp is handled by @CreationTimestamp in model

        ChatSession saved = sessionRepository.save(session);
        log.debug("Session saved with ID: {}", saved.getId());

        return mapToSessionResponse(saved);
    }

    /**
     * Retrieves all chat sessions for a specific user.
     *
     * @param userId the user ID to fetch sessions for
     * @return a list of chat session data transfer objects
     */
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getAllSessions(String userId) {
        log.info("Fetching all chat sessions for user: {}", userId);
        return sessionRepository.findByUserId(userId).stream()
                .map(this::mapToSessionResponse)
                .toList();
    }

    /**
     * Retrieves a session by its ID.
     *
     * @param id the UUID of the session
     * @return the session's data transfer object
     * @throws EntityNotFoundException if session is not found
     */
    @Transactional(readOnly = true)
    public ChatSessionResponse getSession(UUID id) {
        log.debug("Fetching session details for ID: {}", id);
        return sessionRepository.findById(id)
                .map(this::mapToSessionResponse)
                .orElseThrow(() -> {
                    log.error("Session not found: {}", id);
                    return new EntityNotFoundException("Session not found: " + id);
                });
    }

    /**
     * Updates an existing session's metadata.
     *
     * @param id      the UUID of the session
     * @param updates the updates to apply
     * @return the updated session's data transfer object
     */
    public ChatSessionResponse updateSession(UUID id, UpdateSessionRequest updates) {
        log.info("Updating session: {}", id);

        ChatSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + id));

        if (updates.getTitle() != null) {
            log.debug("Changing title from '{}' to '{}'", session.getTitle(), updates.getTitle());
            session.setTitle(updates.getTitle());
        }

        if (updates.getIsFavorite() != null) {
            log.debug("Setting isFavorite to: {}", updates.getIsFavorite());
            session.setFavorite(updates.getIsFavorite());
        }

        ChatSession updated = sessionRepository.save(session);
        return mapToSessionResponse(updated);
    }

    /**
     * Deletes a session and all its cascades.
     *
     * @param id the UUID of the session
     */
    public void deleteSession(UUID id) {
        log.info("Deleting chat session and history for ID: {}", id);
        if (!sessionRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent session: {}", id);
            throw new EntityNotFoundException("Session not found: " + id);
        }
        sessionRepository.deleteById(id);
        log.debug("Session {} deleted successfully.", id);
    }

    /**
     * Appends a new message to a session's history.
     *
     * @param sessionId the target session ID
     * @param request   the message content and role
     * @return the created message's data transfer object
     */
    public ChatMessageResponse addMessage(UUID sessionId, ChatMessageRequest request) {
        log.info("Adding {} message to session: {}", request.getRole(), sessionId);

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));

        // Create new message entity
        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setRole(request.getRole());
        message.setContent(request.getContent());
        message.setContext(request.getContext());

        ChatMessage saved = messageRepository.save(message);
        log.debug("Message saved with ID: {}", saved.getId());

        return mapToMessageResponse(saved);
    }

    /**
     * Retrieves a paginated history of messages for a session.
     *
     * @param sessionId the UUID of the session
     * @param pageable  pagination details
     * @return a page of message data transfer objects
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getMessages(UUID sessionId, Pageable pageable) {
        log.debug("Fetching messages for session: {} | Page: {}", sessionId, pageable.getPageNumber());

        if (!sessionRepository.existsById(sessionId)) {
            throw new EntityNotFoundException("Session not found: " + sessionId);
        }

        return messageRepository.findBySessionIdOrderByTimestampAsc(sessionId, pageable)
                .map(this::mapToMessageResponse);
    }

    /**
     * Maps a ChatSession entity to a ChatSessionResponse DTO.
     */
    private ChatSessionResponse mapToSessionResponse(ChatSession session) {
        ChatSessionResponse response = new ChatSessionResponse();
        response.setId(session.getId());
        response.setTitle(session.getTitle());
        response.setUserId(session.getUserId());
        response.setFavorite(session.isFavorite());
        response.setCreatedAt(session.getCreatedAt());
        return response;
    }

    /**
     * Maps a ChatMessage entity to a ChatMessageResponse DTO.
     */
    private ChatMessageResponse mapToMessageResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setRole(message.getRole());
        response.setContent(message.getContent());
        response.setContext(message.getContext());
        response.setTimestamp(message.getTimestamp());
        response.setSessionId(message.getSession().getId());
        return response;
    }
}
