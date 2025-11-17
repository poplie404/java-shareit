package ru.practicum.shareit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.shareit")
public class ShareItApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShareItApplication.class, args);
    }
}

