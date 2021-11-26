package com.epam.cdp.m2.hw2.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import javafx.util.Pair;

public class Java7ParallelAggregator implements Aggregator {

  static int numThreads = Runtime.getRuntime().availableProcessors();

  private static Long getWordFrequency(List<String> words, String word) {
    int wordFrequency = 0;
    for (String str : words) {
      if (str.equals(word)) {
        wordFrequency++;
      }
    }
    return (long) wordFrequency;
  }

  private static Map<String, Long> mergeMaps(Map<String, Long> firstMap,
      Map<String, Long> secondMap) {

    for (Map.Entry<String, Long> entry : firstMap.entrySet()) {
      String key = entry.getKey();
      long val = entry.getValue();
      secondMap.put(key, (secondMap.containsKey(key) ? secondMap.get(key) + val : val));
    }
    return secondMap;
  }

  @Override
  public int sum(List<Integer> numbers) {
    ForkJoinPool pool = new ForkJoinPool(numThreads);
    int threshold = numbers.size() / numThreads;

    return pool.invoke(new SumTask(numbers, threshold));
  }

  @Override
  public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {
    ForkJoinPool pool = new ForkJoinPool(numThreads);
    int threshold = words.size() / numThreads;
    long counter = 0;
    Map<String, Long> map = pool.invoke(new PairTask(words, threshold));
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
    ForkJoinPool pool = new ForkJoinPool(numThreads);
    int threshold = words.size() / numThreads;
    Map<String, Boolean> map = pool.invoke(new DuplicatesTask(words, threshold));
    List<Map.Entry<String, Boolean>> duplicates = new LinkedList<>(map.entrySet());

    List<String> limitedList = new ArrayList<>();

    for (Map.Entry<String, Boolean> entry : duplicates) {
      if (entry.getValue()) {
        limitedList.add(entry.getKey());
      }
    }

    limitedList.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        int lengthsComparison = Integer.compare(o1.length(), o2.length());
        if (lengthsComparison == 0) {
          return o1.compareTo(o2);
        }
        return lengthsComparison;
      }
    });
    return limitedList.size() < limit ? limitedList : limitedList.subList(0, (int) limit);
  }

  static class SumTask extends RecursiveTask<Integer> {

    List<Integer> numbers;
    int threshold;

    public SumTask(List<Integer> numbers, int threshold) {
      this.numbers = numbers;
      this.threshold = threshold;
    }

    @Override
    protected Integer compute() {
      int sum = 0;

      if (numbers.isEmpty()) {
        return 0;
      } else if (numbers.size() <= ((numbers.size() < numThreads) ? numThreads : threshold)) {
        for (Integer i : numbers) {
          sum += i;
        }
        return sum;

      } else {
        int middle = numbers.size() / 2;
        List<Integer> firstSubList = numbers.subList(0, middle);
        List<Integer> secondSubList = numbers.subList(middle, numbers.size());
        SumTask firstTask = new SumTask(firstSubList, threshold);
        firstTask.fork();
        SumTask secondTask = new SumTask(secondSubList, threshold);
        int secondValue = secondTask.compute();

        return firstTask.join() + secondValue;
      }
    }
  }

  static class PairTask extends RecursiveTask<Map<String, Long>> {

    List<String> words;
    int threshold;

    public PairTask(List<String> words, int threshold) {
      this.words = words;
      this.threshold = threshold;
    }

    @Override
    protected Map<String, Long> compute() {
      if (words.size() <= ((words.size() < numThreads) ? numThreads : threshold)) {
        HashMap<String, Long> map = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
          map.put(words.get(i), getWordFrequency(words, words.get(i)));
        }
        return map;
      } else {
        int middle = words.size() / 2;
        List<String> wordsSubListOne = words.subList(0, middle);
        PairTask one = new PairTask(wordsSubListOne, threshold);
        one.fork();
        List<String> wordsSubListTwo = words.subList(middle, words.size());
        PairTask two = new PairTask(wordsSubListTwo, threshold);

        return mergeMaps(one.join(), two.compute());
      }
    }
  }

  static class DuplicatesTask extends RecursiveTask<Map<String, Boolean>> {

    List<String> words;
    int threshold;

    public DuplicatesTask(List<String> words, int threshold) {
      this.words = words;
      this.threshold = threshold;
    }

    @Override
    protected Map<String, Boolean> compute() {
      if (words.size() <= ((words.size() < numThreads) ? numThreads : threshold)) {

        return getDuplicated();

      } else {
        Map<String, Boolean> mDuplicatedWords = new HashMap<>();
        List<DuplicatesTask> lstSublists = (List<DuplicatesTask>) divideDuplicatedWordTasks();
        for (DuplicatesTask recursiveTask : lstSublists) {
          Map<String, Boolean> mDuplicatedWordsTemp = recursiveTask.invoke();
          for (Map.Entry<String, Boolean> entry : mDuplicatedWordsTemp.entrySet()) {
            mDuplicatedWords.put(entry.getKey(),
                entry.getValue() || mDuplicatedWords.containsKey(entry.getKey()));
          }
        }
        return mDuplicatedWords;
      }
    }

    private Map<String, Boolean> getDuplicated() {
      Map<String, Boolean> mDuplicatedWords = new HashMap<>();
      for (String word : words) {
        String upperWord = word.toUpperCase();
        if (mDuplicatedWords.containsKey(upperWord)) {
          mDuplicatedWords.put(upperWord, Boolean.TRUE);
        } else {
          mDuplicatedWords.put(upperWord, Boolean.FALSE);
        }
      }
      return mDuplicatedWords;
    }

    private Collection<DuplicatesTask> divideDuplicatedWordTasks() {
      List<DuplicatesTask> lstSublists = new ArrayList<>();
      lstSublists.add(new DuplicatesTask(words.subList(0, words.size() / 2), threshold));
      lstSublists.add(new DuplicatesTask(words.subList(words.size() / 2, words.size()), threshold));
      return lstSublists;
    }
  }
}
