package com.pipeline;

import com.pipeline.models.TimeseriesReading;
import java.time.Duration;
import java.util.Properties;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.formats.avro.AvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public class TimeseriesAnalysisJob {
    public static void main(String[] args) {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        var kafkaSource =
                new FlinkKafkaConsumer<>(
                        "ts-events",
                        AvroDeserializationSchema.forSpecific(TimeseriesReading.class),
                        properties);

        // TODO: Kafka-partition-aware strategy?
        //
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/event_timestamps_watermarks.html#watermark-strategies-and-the-kafka-connector
        kafkaSource.assignTimestampsAndWatermarks(
                WatermarkStrategy.<TimeseriesReading>forBoundedOutOfOrderness(
                                Duration.ofSeconds(20))
                        .withTimestampAssigner((event, timestamp) -> event.getTimestamp()));

        DataStream<TimeseriesReading> stream = env.addSource(kafkaSource);
    }
}
