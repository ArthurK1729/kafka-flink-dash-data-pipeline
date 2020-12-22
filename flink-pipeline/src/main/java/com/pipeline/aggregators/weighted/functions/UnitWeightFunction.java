package com.pipeline.aggregators.weighted.functions;

public class UnitWeightFunction implements WeightFunction {
    @Override
    public Double computeWeight(Double value, Long position, Long windowSize) {
        return 1.0;
    }
}
