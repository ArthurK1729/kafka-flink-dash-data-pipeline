package com.pipeline.testutils;

import com.pipeline.aggregators.weighted.functions.WeightFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction.Context;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BaseWindowTest {

    protected @Mock Context mockedContext;
    protected @Mock WeightFunction weightFunction;
}
