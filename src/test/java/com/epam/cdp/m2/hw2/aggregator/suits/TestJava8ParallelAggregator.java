package com.epam.cdp.m2.hw2.aggregator.suits;

import com.epam.cdp.m2.hw2.aggregator.duplicates.impl.Java8ParallelAggregatorDuplicatesTest;
import com.epam.cdp.m2.hw2.aggregator.frequency.impl.Java8ParallelAggregatorFrequencyTest;
import com.epam.cdp.m2.hw2.aggregator.sum.impl.Java8ParallelAggregatorSumTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Java8ParallelAggregatorFrequencyTest.class,
        Java8ParallelAggregatorSumTest.class,
        Java8ParallelAggregatorDuplicatesTest.class,
})
public class TestJava8ParallelAggregator {

}
