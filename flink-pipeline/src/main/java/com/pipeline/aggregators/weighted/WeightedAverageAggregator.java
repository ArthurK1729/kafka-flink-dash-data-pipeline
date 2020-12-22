package com.pipeline.aggregators.weighted;

import com.pipeline.models.TimeseriesReading;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.apache.flink.shaded.curator4.com.google.common.collect.Streams;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.IterableUtils;

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

        var readings = IterableUtils.toStream(elements).collect(Collectors.toList());
        var count = Long.valueOf(readings.size());
        var sortedReadings =
                readings.stream().sorted(Comparator.comparingLong(TimeseriesReading::getTimestamp));

        var weightedSum =
                Streams.mapWithIndex(
                                sortedReadings,
                                (reading, index) ->
                                        this.weightFunction.computeWeight(
                                                reading.getReading(), index, count))
                        .reduce(0.0, Double::sum);

        out.collect(weightedSum / count.doubleValue());
    }
}
