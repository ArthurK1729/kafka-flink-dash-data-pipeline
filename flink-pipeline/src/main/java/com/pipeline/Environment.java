package com.pipeline;

import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class Environment {
    @Nullable private final String brokerAddress;
    @Nullable private final String topic;
    @Nullable private final String consumerGroup;
    @Nullable private final String localMode;

    public static Environment fromEnv() {
        return Environment.builder()
                .brokerAddress(System.getenv("BROKER_ADDRESS"))
                .topic(System.getenv("TOPIC"))
                .localMode(System.getenv("LOCAL_MODE"))
                .consumerGroup(System.getenv("CONSUMER_GROUP"))
                .build();
    }

    public Optional<String> getBrokerAddress() {
        return Optional.ofNullable(brokerAddress);
    }

    public Optional<String> getTopic() {
        return Optional.ofNullable(topic);
    }

    public Optional<String> getLocalMode() {
        return Optional.ofNullable(localMode);
    }

    public Optional<String> getConsumerGroup() {
        return Optional.ofNullable(consumerGroup);
    }
}
