package com.pipeline;

import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class Environment {
    @Nullable private final String brokerAddress;

    public static Environment fromEnv() {
        return Environment.builder().brokerAddress(System.getenv("BROKER_ADDRESS")).build();
    }

    public Optional<String> getBrokerAddress() {
        return Optional.ofNullable(brokerAddress);
    }
}
