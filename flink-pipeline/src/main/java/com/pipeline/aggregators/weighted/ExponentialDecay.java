package com.pipeline.aggregators.weighted;

public class ExponentialDecay implements WeightFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public Double computeWeight(Double value, Long position, Long windowSize) {
        var decayFactor = Math.exp(-position.doubleValue() / windowSize.doubleValue());
        return value * decayFactor;
    }
}
