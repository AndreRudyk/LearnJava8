package com.epam.cdp.m2.hw2.aggregator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class Java8Aggregator implements Aggregator {

  private static final Comparator<String> comparator = ((v1, v2) -> {
    int res = Integer.compare(v1.length(), v2.length());
    if (res == 0) {
      return v1.compareTo(v2);
    }
    return res;
  });

  @Override
  public int sum(List<Integer> numbers) {
    return numbers.stream()
        .mapToInt(Integer::intValue)
        .sum();
  }

  @Override
  public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {
    return words.stream().collect(
            Collectors.toMap(Function.identity(), value -> 1L,
                (k1, k2) -> k1 + 1, LinkedHashMap::new))
        .entrySet().stream()
        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
        .limit(limit)
        .map(e -> new Pair<>(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }

  @Override
  public List<String> getDuplicates(List<String> words, long limit) {
    HashSet<String> set = new HashSet<>();
    return words.stream()
        .map(String::toUpperCase)
        .sorted(comparator)
        .filter(e -> !set.add(e))
        .limit(limit)
        .collect(Collectors.toList());
  }
}