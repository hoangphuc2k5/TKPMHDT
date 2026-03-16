package com.example.nuocuong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NuocUongApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuocUongApplication.class, args);
    }

}
