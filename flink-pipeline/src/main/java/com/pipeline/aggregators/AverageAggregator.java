package com.pipeline.aggregators;

import com.pipeline.models.TimeseriesReading;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class AverageAggregator
        extends ProcessAllWindowFunction<TimeseriesReading, Double, TimeWindow> {
    private static final long serialVersionUID = 1L;

    @Override
    public void process(
            Context context, Iterable<TimeseriesReading> elements, Collector<Double> out) {
        var sum = 0.0;
        var count = 0;

        for (TimeseriesReading reading : elements) {
            sum += reading.getReading();
            count += 1;
        }

        out.collect(sum / count);
    }
}
