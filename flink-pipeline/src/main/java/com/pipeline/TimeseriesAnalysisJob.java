package com.pipeline;

import com.pipeline.models.TimeseriesReading;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.formats.avro.AvroDeserializationSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class TimeseriesAnalysisJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        var source = getLocalSource(env).name("source");

        var stream =
                source.assignTimestampsAndWatermarks(
                        WatermarkStrategy.<TimeseriesReading>forBoundedOutOfOrderness(
                                        Duration.ofSeconds(1))
                                .withTimestampAssigner((event, timestamp) -> event.getTimestamp()));

        var aggregatedReadings =
                stream.windowAll(SlidingEventTimeWindows.of(Time.seconds(3), Time.seconds(1)))
                        .process(new AverageAggregator())
                        .name("aggregatedTimeseriesReadings");

        // StreamingFileSink -- Maciej's article
        aggregatedReadings
                .setParallelism(1)
                .addSink(new PrintSinkFunction<>(false))
                .name("aggregationOutput");

        if (System.getenv("BROKER_ADDRESS") != null) {
            aggregatedReadings.setParallelism(1).writeAsText("/data/output.txt");
        } else {
            aggregatedReadings.setParallelism(1).writeAsText("/tmp/data/output.txt");
        }

        env.execute(TimeseriesAnalysisJob.class.getName());
    }

    private static DataStreamSource<TimeseriesReading> getLocalSource(
            StreamExecutionEnvironment env) {
        var beginningOfTime = 1607805624L;

        return env.fromCollection(
                List.of(
                        new TimeseriesReading(0, 2.5, beginningOfTime),
                        new TimeseriesReading(0, 2.38, beginningOfTime + 1),
                        new TimeseriesReading(0, 10.0, beginningOfTime + 2),
                        new TimeseriesReading(0, 10002.334, beginningOfTime + 3),
                        new TimeseriesReading(0, 8893.3, beginningOfTime + 4),
                        new TimeseriesReading(0, 3.3, beginningOfTime + 5)));
    }

    private static DataStreamSource<TimeseriesReading> getKafkaSource(
            StreamExecutionEnvironment env) {
        // TODO: kafka partition aware
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/event_timestamps_watermarks.html#watermark-strategies-and-the-kafka-connector
        Properties properties = new Properties();
        properties.setProperty(
                "bootstrap.servers",
                Optional.ofNullable(System.getenv("BROKER_ADDRESS")).orElse("localhost:9092"));
        properties.setProperty("group.id", "test");
        properties.setProperty("enable.auto.commit", "false");

        var kafkaSource =
                new FlinkKafkaConsumer<>(
                        "ts-events",
                        AvroDeserializationSchema.forSpecific(TimeseriesReading.class),
                        properties);

        return env.addSource(kafkaSource);
    }
}
