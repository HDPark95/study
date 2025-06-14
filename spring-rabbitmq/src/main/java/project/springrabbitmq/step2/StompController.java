package project.springrabbitmq.step2;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class StompController {
    private final SimpMessagingTemplate messagingTemplate;

    public StompController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/send")
    public void sendMessage(NotificationMessage notificationMessage) {
        String message = notificationMessage.getMessage();
        System.out.println("[#] message = " + message);
        messagingTemplate.convertAndSend("/topic/notification", message);
    }
}
