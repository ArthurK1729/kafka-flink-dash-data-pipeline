package com.pipeline.aggregators.weighted;

import java.io.Serializable;

public interface WeightFunction extends Serializable {
    Double computeWeight(Double value, Long position, Long windowSize);
}
