package com.demandeAssistance.demandeAssistance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(messagingTemplate);
    }

    @Test
    void notifyLogisticien_ShouldSendMessageToTopic() {
        String message = "Un nouveau dossier a été créé";

        notificationService.notifyLogisticien(message);

        verify(messagingTemplate).convertAndSend("/topic/logisticien", message);
        verifyNoMoreInteractions(messagingTemplate);
    }
}