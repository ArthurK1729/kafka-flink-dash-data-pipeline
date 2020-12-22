package com.pipeline.jobs;

import com.pipeline.Environment;
import com.pipeline.aggregators.weighted.functions.ExponentialDecayWeightFunction;
import com.pipeline.aggregators.weighted.WeightedAverageAggregator;
import com.pipeline.models.TimeseriesReading;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;
import java.util.logging.Logger;

import static com.pipeline.FlinkUtils.getLocalSource;
import static com.pipeline.FlinkUtils.getSink;
import static com.pipeline.FlinkUtils.getTempFlinkPath;
import static org.apache.flink.streaming.api.TimeCharacteristic.EventTime;

public class TimeseriesAnalysisJob {
    private static final Logger LOGGER = Logger.getLogger(TimeseriesAnalysisJob.class.getName());
    private static final Environment envConfig = Environment.fromEnv();

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(EventTime);

        // var source =
        //        getKafkaSource(
        //                        env,
        //                        envConfig
        //                                .getTopic()
        //                                .orElseThrow(
        //                                        () ->
        //                                                new RuntimeException(
        //                                                        "TOPIC environment variable must
        // be set")),
        //                        envConfig.getBrokerAddress().orElse("localhost:9092"),
        //                        envConfig.getConsumerGroup().orElse("default-consumer-group"))
        //                .name("source");

        var source = getLocalSource(env);

        var timestampedStream =
                source.assignTimestampsAndWatermarks(
                        WatermarkStrategy.<TimeseriesReading>forBoundedOutOfOrderness(
                                        Duration.ofSeconds(1))
                                .withTimestampAssigner((event, timestamp) -> event.getTimestamp()));

        var aggregatedReadings =
                timestampedStream
                        .windowAll(SlidingEventTimeWindows.of(Time.seconds(3), Time.seconds(1)))
                        .process(
                                new WeightedAverageAggregator(new ExponentialDecayWeightFunction()))
                        .name("aggregatedTimeseriesReadings");

        envConfig
                .getLocalMode()
                .ifPresentOrElse(
                        (localMode) -> {
                            LOGGER.warning("Running in local mode.");
                            aggregatedReadings
                                    .setParallelism(1)
                                    .addSink(new PrintSinkFunction<>(false))
                                    .name("aggregationOutput");

                            aggregatedReadings
                                    .addSink(getSink(getTempFlinkPath("data")))
                                    .name("sink");
                        },
                        () -> aggregatedReadings.addSink(getSink(new Path("/data"))).name("sink"));

        env.execute(TimeseriesAnalysisJob.class.getName());
    }
}
