package dev.leoduarte.librarykafkaproducer.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {

    @Value("${leoduarte.customconfig.newtopicname}")
    private String topicName;

    @Value("${leoduarte.customconfig.partition}")
    private Integer partition;

    @Value("${leoduarte.customconfig.replicas}")
    private Integer replicas;

    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder
                .name(topicName)
                .partitions(partition)
                .replicas(replicas)
                .build();
    }
}
