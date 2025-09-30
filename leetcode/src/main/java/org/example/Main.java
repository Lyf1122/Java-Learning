package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> countMap = new HashMap<>();
        String numberString = in.nextLine();
        String[] numbers = numberString.split(" ");
        List<Integer> numberList = Arrays.stream(numbers).map(Integer::parseInt).collect(Collectors.toList());
        numberList.forEach(
          num -> countMap.put(num, countMap.getOrDefault(num, 0) + 1)
        );

        Map<Integer, Integer> remaining = new HashMap<>(countMap);

        List<Integer> bombs = new ArrayList<>();
        for (int num : numberList) {
            if (remaining.getOrDefault(num, 0) >= 4) {
                bombs.add(num);
            }
        }
        Collections.sort(bombs, Collections.reverseOrder());

        for (int num : bombs) {
            for (int i=0; i<4; i++) {
                result.add(num);
            }
            remaining.put(num, remaining.get(num) - 4);
        }

        List<int[]> fullHouse = new ArrayList<>();
        List<Integer> potentialTri = new ArrayList<>();
        List<Integer> potentialPairs = new ArrayList<>();

        for (int num : numberList) {
            int count = remaining.getOrDefault(num, 0);
            if (count >= 3) {
                potentialTri.add(num);
            }
            if (count >= 2) {
                potentialPairs.add(num);
            }
        }
        Collections.sort(potentialTri, Collections.reverseOrder());
        Collections.sort(potentialPairs, Comparator.reverseOrder());

        int i=0, j=0;
        while (i < potentialTri.size() && j < potentialPairs.size()) {
            int triNum = potentialTri.get(i);
            int pairNum = potentialPairs.get(j);
            if (triNum != pairNum &&
              remaining.getOrDefault(triNum, 0) >=3 &&
              remaining.getOrDefault(pairNum, 0) >=2
            ) {
                fullHouse.add(new int[]{triNum, pairNum});
                remaining.put(triNum, remaining.get(triNum) - 3);
                remaining.put(pairNum, remaining.get(pairNum) - 2);
                i++;j++;
            } else if (triNum == pairNum) {
                if (remaining.getOrDefault(triNum, 0) >=5) {
                    fullHouse.add(new int[] {triNum, pairNum});
                    remaining.put(triNum, remaining.get(triNum) - 5);
                    i++;j++;
                } else {
                    j++;
                }
            } else {
                j++;
            }
        }
        fullHouse.sort((a, b) -> Integer.compare(b[0], a[0]));
        for (int[] fh : fullHouse) {
            for (int k=0; k<3; k++) {
                result.add(fh[0]);
            }
            for (int k=0; k<2; k++) {
                result.add(fh[1]);
            }
        }

        List<Integer> triples = new ArrayList<>();
        for (int num : numberList) {
            if (remaining.getOrDefault(num, 0) >= 3) {
                triples.add(num);
            }
        }
        Collections.sort(triples, Collections.reverseOrder());
        for (int num:triples) {
            for (int k=0; k<3; k++) {
                result.add(num);
            }
            remaining.put(num, remaining.get(num) - 3);
        }

        List<Integer> pairs = new ArrayList<>();
        for (int num : numberList) {
            if (remaining.getOrDefault(num, 0) >= 2) {
                pairs.add(num);
            }
        }
        Collections.sort(pairs, Collections.reverseOrder());
        for (int num:pairs) {
            for (int k=0; k<2; k++) {
                result.add(num);
            }
            remaining.put(num, remaining.get(num) - 2);
        }

        List<Integer> singles = new ArrayList<>();
        for (int num : numberList) {
            int count = remaining.getOrDefault(num, 0);
            if (count > 0) {
                result.add(num);
            }
        }
        Collections.sort(singles, Collections.reverseOrder());
        for (int num:singles) {
            int count = remaining.get(num);
            for (int k=0; k<count; k++) {
                result.add(num);
            }
            remaining.put(num, remaining.get(num) - 3);
        }

        // 炸弹
//        addFreq(result, countMap, 4);
//        // Full house
//        addFullHouseFreq(result, countMap);
//        // Triple
//        addFreq(result, countMap, 3);
        // Pairs
//        addFreq(result, countMap, 2);
//        addSingles(result, countMap);
        for (int p = 0; p < result.size(); p++) {
            if (p != result.size()-1) {
                System.out.print(result.get(p) + " ");
            } else {
                System.out.print(result.get(p));
            }
        }
    }

    static void addFreq(List<Integer> result, Map<Integer, Integer> map, int targetFreq) {
        List<Integer> matchList = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == targetFreq) {
                matchList.add(entry.getKey());
            }

        }
        // DESC
        matchList.sort(Collections.reverseOrder());
        for (int num : matchList) {
            for (int i=0; i<targetFreq; i++) {
                result.add(num);
            }
            map.remove(num);
        }
    }

    static void addFullHouseFreq(List<Integer> result, Map<Integer, Integer> freqMap) {
        List<Integer> triple = new ArrayList<>();
        List<Integer> pairs = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() == 3) {
                triple.add(entry.getKey());
            }
            if (entry.getValue() == 2) {
                pairs.add(entry.getKey());
            }
        }
        triple.sort(Comparator.reverseOrder());
        pairs.sort(Comparator.reverseOrder());

        int pairToUse = Math.min(triple.size(), pairs.size());
        for (int i = 0; i<pairToUse; i++) {
            for (int j=0; j<3; j++) {
                result.add(triple.get(i));
            }
            for (int k=0; k<2; k++) {
                result.add(pairs.get(i));
            }
            freqMap.remove(triple.get(i));
            freqMap.remove(pairs.get(i));
        }
    }

    static void addSingles(List<Integer> result, Map<Integer, Integer> freqMap) {
        List<Integer> singles = new ArrayList<>(new ArrayList<>(freqMap.keySet()));
        singles.sort(Collections.reverseOrder());
        for (int num : singles) {
            for (int i=0; i < freqMap.get(num); i++) {
                result.add(num);
            }
        }
    }

}