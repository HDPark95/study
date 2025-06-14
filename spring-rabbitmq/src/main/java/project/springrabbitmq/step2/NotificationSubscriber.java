package project.springrabbitmq.step2;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationSubscriber {

    public static final String CLIENT_URL = "/topic/notifications";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationSubscriber(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void subscribe(String message) {
        simpMessagingTemplate.convertAndSend(CLIENT_URL, message);
    }
}
