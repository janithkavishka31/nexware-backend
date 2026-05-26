package com.nexware.config;

import java.util.List;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "nexware.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTopicConfig {

    public static final String INVENTORY_UPDATED = "nexware.inventory.updated";
    public static final String STOCK_RESERVED = "nexware.stock.reserved";
    public static final String TRANSFER_COMPLETED = "nexware.transfer.completed";
    public static final String PURCHASE_ORDER_APPROVED = "nexware.purchase-order.approved";
    public static final String LOW_STOCK_DETECTED = "nexware.low-stock.detected";

    @Bean
    List<NewTopic> nexwareTopics() {
        return List.of(
                topic(INVENTORY_UPDATED),
                topic(STOCK_RESERVED),
                topic(TRANSFER_COMPLETED),
                topic(PURCHASE_ORDER_APPROVED),
                topic(LOW_STOCK_DETECTED)
        );
    }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
