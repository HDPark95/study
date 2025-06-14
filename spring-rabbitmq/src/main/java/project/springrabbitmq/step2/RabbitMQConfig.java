package project.springrabbitmq.step2;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "notificationQueue";
    public static final String FANOUT_EXCHANGE = "notificationExchange";
    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding bindNotification(Queue notificationQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(notificationQueue).to(fanoutExchange);
    }
}
