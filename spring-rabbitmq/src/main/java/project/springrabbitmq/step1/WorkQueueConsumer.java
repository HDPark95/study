package project.springrabbitmq.step1;

import org.springframework.stereotype.Component;

@Component
public class WorkQueueConsumer {

    public void workQueueTask(String message){
        String[] messageParts = message.split("\\|");
        String originMessage = messageParts[0];
        int duration = Integer.parseInt(messageParts[1]);

        System.out.println("# Received : " + originMessage + " duration : " + duration);
        try{
            int seconds = duration / 1000;
            for (int i = 0; i < seconds; i++) {
                Thread.sleep(1000);
                System.out.println(".");
            }
            Thread.sleep(duration);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        System.out.println("\n# Complete : " + originMessage);
    }

}
