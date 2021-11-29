package com.epam.cdp.m2.hw2.aggregator;

import javafx.util.Pair;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Java8ParallelAggregator implements Aggregator {

    @Override
    public int sum(List<Integer> numbers) {
        return numbers.parallelStream()
                .reduce(0, Integer::sum);
    }

    @Override
    public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {
        return words.parallelStream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().parallelStream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDuplicates(List<String> words, long limit) {
        HashSet<String> set = new HashSet<>();
        return words.parallelStream()
                .map(String::toUpperCase)
                .filter(e -> !set.add(e))
                .collect(Collectors.toSet())
                .stream()
                .sorted(Comparator.comparing(String::length).thenComparing(i -> i))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
