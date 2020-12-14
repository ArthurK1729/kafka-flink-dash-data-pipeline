package com.pipeline;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ToString
@Getter
@Builder
public class Environment {
    private final Optional<String> brokerAddress;

    public static Environment fromEnv() {
        return Environment.builder()
                .brokerAddress(Optional.ofNullable(System.getenv("BROKER_ADDRESS")))
                .build();
    }
}
