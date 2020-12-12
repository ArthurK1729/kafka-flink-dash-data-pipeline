package com.pipeline;

import com.pipeline.models.TimeseriesReading;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.formats.avro.AvroDeserializationSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public class TimeseriesAnalysisJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        var source = getLocalSource(env).name("source");

        var stream =
                source.assignTimestampsAndWatermarks(
                        WatermarkStrategy.<TimeseriesReading>forBoundedOutOfOrderness(
                                        Duration.ofSeconds(2))
                                .withTimestampAssigner((event, timestamp) -> event.getTimestamp()));

        var aggregatedReadings =
                stream.windowAll(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(3)))
                        .process(new AverageAggregator())
                        .name("aggregatedTimeseriesReadings");

        aggregatedReadings.addSink(new PrintSinkFunction<>(false)).name("aggregationOutput");

        env.execute(TimeseriesAnalysisJob.class.getName());
    }

    private static DataStreamSource<TimeseriesReading> getLocalSource(
            StreamExecutionEnvironment env) {
        return env.fromCollection(
                List.of(
                        new TimeseriesReading(2.5, 1607805624),
                        new TimeseriesReading(2.38, 1607805624 + 1),
                        new TimeseriesReading(10.0, 1607805624 + 2),
                        new TimeseriesReading(10002.334, 1607805624 + 3),
                        new TimeseriesReading(8893.3, 1607805624 + 4),
                        new TimeseriesReading(3.3, 1607805624 + 5)));
    }

    private static DataStreamSource<TimeseriesReading> getKafkaSource(
            StreamExecutionEnvironment env) {
        // TODO: kafka partition aware
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/event_timestamps_watermarks.html#watermark-strategies-and-the-kafka-connector
        Properties properties = new Properties();
        // TODO: Do something about auto-commit here
        properties.setProperty("bootstrap.servers", "broker:29092");
        properties.setProperty("group.id", "test");

        var kafkaSource =
                new FlinkKafkaConsumer<>(
                        "ts-events",
                        AvroDeserializationSchema.forSpecific(TimeseriesReading.class),
                        properties);

        return env.addSource(kafkaSource);
    }
}
