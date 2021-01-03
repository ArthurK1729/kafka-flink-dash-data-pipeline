package com.pipeline;

import com.pipeline.models.TimeseriesReading;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.AvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public class FlinkUtils {
    public static Path getTempFlinkPath(String tempDirectoryName) {
        var tmpPath = Paths.get(System.getProperty("java.io.tmpdir"), tempDirectoryName);
        return new Path(tmpPath.toString());
    }

    public static DataStreamSource<TimeseriesReading> getKafkaSource(
            StreamExecutionEnvironment env,
            String topic,
            String brokerAddress,
            String consumerGroup) {
        Properties properties = new Properties();

        properties.putAll(
                ofEntries(
                        entry("bootstrap.servers", brokerAddress),
                        entry("group.id", consumerGroup),
                        entry("enable.auto.commit", "false")));

        var kafkaSource =
                new FlinkKafkaConsumer<>(
                        topic,
                        AvroDeserializationSchema.forSpecific(TimeseriesReading.class),
                        properties);

        return env.addSource(kafkaSource);
    }

    public static DataStreamSource<TimeseriesReading> getLocalSource(
            StreamExecutionEnvironment env) {
        var beginningOfTime = 1607805624L;

        return env.fromCollection(
                List.of(
                        new TimeseriesReading(0, 2.5, beginningOfTime),
                        new TimeseriesReading(0, 2.38, beginningOfTime + 1),
                        new TimeseriesReading(0, 10.0, beginningOfTime + 2),
                        new TimeseriesReading(0, 10002.334, beginningOfTime + 3),
                        new TimeseriesReading(0, 8893.3, beginningOfTime + 4),
                        new TimeseriesReading(0, 3.3, beginningOfTime + 5),
                        new TimeseriesReading(1, 10.5, beginningOfTime),
                        new TimeseriesReading(1, 8.38, beginningOfTime + 1),
                        new TimeseriesReading(1, 1232.0, beginningOfTime + 2),
                        new TimeseriesReading(1, 5.334, beginningOfTime + 3),
                        new TimeseriesReading(1, 22.3, beginningOfTime + 4),
                        new TimeseriesReading(1, 332.113, beginningOfTime + 5)));
    }

    public static <T> SinkFunction<T> getSink(Path path) {
        return StreamingFileSink.forRowFormat(path, new SimpleStringEncoder<T>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(TimeUnit.SECONDS.toMillis(600))
                                .withInactivityInterval(TimeUnit.SECONDS.toMillis(30))
                                .withMaxPartSize(1024 * 1024 * 1024)
                                .build())
                .build();
    }
}
