package project.springrabbitmq.step1;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WorkQueueController {

    private final WorkQueueProducer workQueueProducer;

    public WorkQueueController(WorkQueueProducer workQueueProducer) {
        this.workQueueProducer = workQueueProducer;
    }

    @PostMapping("/workqueue")
    public String workQueue(@RequestParam String message, @RequestParam int duration) {
        workQueueProducer.sendWorkQueue(message, duration);
        return "Work queue snet = " + message + ", duration = " + duration;
    }
}
