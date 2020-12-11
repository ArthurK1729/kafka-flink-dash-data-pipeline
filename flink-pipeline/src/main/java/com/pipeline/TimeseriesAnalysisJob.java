package com.pipeline;

import com.pipeline.models.TimeseriesReading;
import java.util.Properties;
import org.apache.flink.formats.avro.AvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public class TimeseriesAnalysisJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        DataStream<TimeseriesReading> stream =
                env.addSource(
                        new FlinkKafkaConsumer<>(
                                "ts-events",
                                AvroDeserializationSchema.forSpecific(TimeseriesReading.class),
                                properties));
    }
}
