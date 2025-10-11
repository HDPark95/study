package project.cucumber.runner;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import project.cucumber.CucumberPracticeApplication;

@CucumberContextConfiguration
@SpringBootTest(classes = CucumberPracticeApplication.class)
public class CucumberSpringConfiguration {
}
