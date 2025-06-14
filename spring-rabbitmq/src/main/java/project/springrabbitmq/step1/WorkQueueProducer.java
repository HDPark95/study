package project.springrabbitmq.step1;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public WorkQueueProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendWorkQueue(String workQueue, int duration) {
        String message = workQueue + "|" + duration;
        rabbitTemplate.convertAndSend(RabbitMQConfigV2.QUEUE_NAME, message);
        System.out.println("# Sent workqueue : " + workQueue + " duration : " + duration);
    }
}
