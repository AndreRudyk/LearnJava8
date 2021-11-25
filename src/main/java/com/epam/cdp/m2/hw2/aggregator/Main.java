package com.epam.cdp.m2.hw2.aggregator;

import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static Map<String, Long> getMostFrequentWords(List<String> words) {
        return  words.stream().collect(
                Collectors.toMap(Function.identity(), value -> 1L,
                    (k1, k2) -> k1 + 1, LinkedHashMap::new));
    }

    public static List<Pair<String, Long>> getPairList(Map<String, Long> map, long limit){
        return map.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map((entry) -> new Pair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private static Long getWordFrequency(List<String> words, String word) {
        int wordFrequency = 0;
        for (String str : words) {
            if(str.equals(word)) {
                wordFrequency ++;
            }
        }
        return (long) wordFrequency;
    }

    private static Comparator<Map.Entry<String, Long>> comparator = (e1, e2) -> (e2.getValue().compareTo(e1.getValue()));

    public static void main(String [] args) {
        List<String> list = Arrays.asList("hello", "hi", "hello", "wazzaup", "hello", "hi");
        Map<String, Long> myMap = getMostFrequentWords(list);
        myMap.forEach((a, b) -> System.out.println(a + " " + b));
        System.out.println("++++++++++++++++++++++++++++++++++++++++");
        List<Pair<String, Long>> pairs = getPairList(myMap, 3);
        pairs.forEach(System.out::println);

    }

}
