package org.generate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.generate"})
public class ApplicationContext {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationContext.class, args);
    }
}