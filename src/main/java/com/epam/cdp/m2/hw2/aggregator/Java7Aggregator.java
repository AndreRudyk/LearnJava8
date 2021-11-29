package com.epam.cdp.m2.hw2.aggregator;

import javafx.util.Pair;

import java.util.*;


public class Java7Aggregator implements Aggregator {

    private static Long getWordFrequency(List<String> words, String word) {
        int wordFrequency = 0;
        for (String str : words) {
            if (str.equals(word)) {
                wordFrequency++;
            }
        }
        return (long) wordFrequency;
    }

    private static Map<String, Integer> getDuplicateWordsMap(List<String> words) {
        HashSet<String> set = new HashSet<>();
        Map<String, Integer> duplicatesMap = new TreeMap<>();

        for (String str : words) {
            if (!set.add(str.toUpperCase())) {
                duplicatesMap.put(str.toUpperCase(), str.length());
            }
        }
        return duplicatesMap;
    }

    @Override
    public int sum(List<Integer> numbers) {
        int sum = 0;
        for (Integer i : numbers) {
            sum += i;
        }
        return sum;
    }

    @Override
    public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {
        long counter = 0;
        HashMap<String, Long> map = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            map.put(words.get(i), getWordFrequency(words, words.get(i)));
        }
        List<Map.Entry<String, Long>> entryList = new LinkedList<>(map.entrySet());
        entryList.sort(new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        List<Pair<String, Long>> pairList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : entryList) {
            if (counter >= limit) {
                break;
            }
            counter++;
            pairList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        return pairList;
    }

    @Override
    public List<String> getDuplicates(List<String> words, long limit) {
        long count = 0;
        List<Map.Entry<String, Integer>> duplicates = new LinkedList<>(
                getDuplicateWordsMap(words).entrySet());
        duplicates.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        List<String> limitedList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : duplicates) {
            if (count >= limit) {
                break;
            }
            count++;
            limitedList.add(entry.getKey());
        }
        return limitedList;
    }

}
