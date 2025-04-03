package com.scittech.city.keycloakmicroservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic sciTechCityTopic() {
        return TopicBuilder.name("sci-tech.city")
            .build();
    }
}
