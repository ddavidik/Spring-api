package com.job.task;

import com.job.task.entity.user.User;
import com.job.task.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            User superman = new User("Superman", "superman@example.com", "93f39e2f-80de-4033-99ee-249d92736a25");
            User batman = new User("Batman", "batman@example.com", "dcb20f8a-5657-4f1b-9f7f-ce65739b359e");
            log.info("Preloading " + userRepository.save(superman));
            log.info("Preloading " + userRepository.save(batman));
        };
    }
}
