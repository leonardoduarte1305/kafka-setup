package dev.leoduarte.librarykafkaconsumer.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.ContainerCustomizer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@EnableKafka
public class LibraryEventsConfig {

//    @Bean
//    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
//            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
//            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory,
//            ObjectProvider<ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>>> kafkaContainerCustomizer,
//            ObjectProvider<SslBundles> sslBundles) {
//
//        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//
//        configurer.configure(factory, kafkaConsumerFactory.getIfAvailable(
//                () -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties(sslBundles.getIfAvailable()))));
//
//        kafkaContainerCustomizer.ifAvailable(factory::setContainerCustomizer);
//        return factory;
//    }

}
