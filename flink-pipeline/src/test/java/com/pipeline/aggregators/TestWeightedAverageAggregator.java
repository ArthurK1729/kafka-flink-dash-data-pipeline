package com.pipeline.aggregators;

import com.pipeline.aggregators.weighted.WeightedAverageAggregator;
import com.pipeline.aggregators.weighted.functions.UnitWeightFunction;
import com.pipeline.models.TimeseriesReading;
import com.pipeline.testutils.BaseWindowTest;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.apache.flink.streaming.api.operators.KeyedProcessOperator;
import org.apache.flink.streaming.runtime.operators.windowing.WindowOperatorTest;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// TODO: test resources
// TODO: jupiter extensions https://www.baeldung.com/junit-5-extensions
// TODO: integration job test https://ci.apache.org/projects/flink/flink-docs-stable/dev/stream/testing.html#testing-flink-jobs
// TODO: do a window test: WindowOperatorTest
public class TestWeightedAverageAggregator extends BaseWindowTest {
    @Test
    public void testAverageComputedCorrectly() {


        WeightedAverageAggregator aggregator =
                new WeightedAverageAggregator(new UnitWeightFunction());



        when(weightFunction.computeWeight(
                        ArgumentMatchers.any(Double.class),
                        ArgumentMatchers.any(Long.class),
                        ArgumentMatchers.any(Long.class)))
                .thenReturn(1.0);

        List<Double> actual = new ArrayList<>();
        List<Double> expected = List.of(2.4);
        ListCollector<Double> listCollector = new ListCollector<>(actual);

        aggregator.process(
                mockedContext, List.of(new TimeseriesReading(1, 2.4, 12L)), listCollector);

        assertThat(actual).isEqualTo(expected);
    }
}
