package project.springrabbitmq.step2;

public class NotificationMessage {
    private String message;

    public NotificationMessage() {
    }

    public NotificationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
