package com.demandeAssistance.demandeAssistance.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyLogisticien(String message) {
        messagingTemplate.convertAndSend("/topic/logisticien", message);
    }
}
