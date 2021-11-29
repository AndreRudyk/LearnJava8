package com.epam.cdp.m2.hw2.aggregator.sum.impl;

import com.epam.cdp.m2.hw2.aggregator.Java7Aggregator;
import com.epam.cdp.m2.hw2.aggregator.sum.JavaAggregatorSumTest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Java7AggregatorSumTest extends JavaAggregatorSumTest {

    public Java7AggregatorSumTest() {
        super(new Java7Aggregator());
    }

}
