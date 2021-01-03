package com.pipeline.aggregators.weighted.functions;

public class ExponentialDecayWeightFunction implements WeightFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public Double computeWeight(Double value, Long position, Long windowSize) {
        return Math.exp(-position.doubleValue() / windowSize.doubleValue());
    }
}
