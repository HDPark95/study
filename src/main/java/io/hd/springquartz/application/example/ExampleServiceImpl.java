package io.hd.springquartz.application.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExampleServiceImpl implements ExampleService{

    @Override
    public void exampleMethod() {
        log.info("Example Method");
    }
}
