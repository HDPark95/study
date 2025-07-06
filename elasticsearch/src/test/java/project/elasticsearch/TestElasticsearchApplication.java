package project.elasticsearch;

import org.springframework.boot.SpringApplication;

public class TestElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.from(ElasticsearchApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
