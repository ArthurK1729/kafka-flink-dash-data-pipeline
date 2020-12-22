package com.pipeline.aggregators.weighted;

import com.pipeline.aggregators.weighted.functions.WeightFunction;
import com.pipeline.models.TimeseriesReading;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.IterableUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

public class WeightedAverageAggregator
        extends ProcessAllWindowFunction<TimeseriesReading, Double, TimeWindow> {
    private static final long serialVersionUID = 1L;

    final WeightFunction weightFunction;

    public WeightedAverageAggregator(WeightFunction weightFunction) {
        this.weightFunction = weightFunction;
    }

    @Override
    public void process(
            Context context, Iterable<TimeseriesReading> elements, Collector<Double> out) {

        var sortedReadings =
                IterableUtils.toStream(elements)
                        .sorted(Comparator.comparingLong(TimeseriesReading::getTimestamp))
                        .collect(Collectors.toList());
        var windowSize = Long.valueOf(sortedReadings.size());

        var index = 0L;
        var weightedSum = 0.0;
        var sumOfWeights = 0.0;

        for (TimeseriesReading reading : sortedReadings) {
            var value = reading.getReading();
            var weight = weightFunction.computeWeight(value, index, windowSize);

            weightedSum += value * weight;
            sumOfWeights += weight;
        }

        out.collect(weightedSum / sumOfWeights);
    }
}
