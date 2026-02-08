package com.backendRole.assignment.service;

import com.backendRole.assignment.dto.ChatSessionResponse;
import com.backendRole.assignment.dto.CreateSessionRequest;
import com.backendRole.assignment.model.ChatSession;
import com.backendRole.assignment.repository.ChatMessageRepository;
import com.backendRole.assignment.repository.ChatSessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    public void testCreateSession() {
        ChatSession session = new ChatSession("Test Session");
        session.setId(UUID.randomUUID());
        session.setCreatedAt(LocalDateTime.now());

        Mockito.when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

        CreateSessionRequest request = new CreateSessionRequest();
        request.setTitle("Test Session");

        ChatSessionResponse created = chatService.createSession(request, "test-user-id");
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("Test Session", created.getTitle());
    }

    @Test
    public void testGetSession_NotFound() {
        Mockito.when(chatSessionRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> chatService.getSession(UUID.randomUUID()));
    }
}
