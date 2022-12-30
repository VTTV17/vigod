package utilities.combine;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Combination {

    // index
    List<int[]> combine(int n, int r) {
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(n, r);
        List<int[]> list = new ArrayList<>();
        while (iterator.hasNext()) {
            final int[] combination = iterator.next();
            list.add(combination);
        }
        return list;
    }

    // strings = {"A", "B", "C"}
    // return: ["A", "B", "C", "AB", "AC", "BC", "ABC"]
    public List<String> getAllCombinations(String[] strings) {
        List<String> list = new ArrayList<>();
        IntStream.range(1, strings.length + 1).forEachOrdered(i -> combine(strings.length, i).stream().map(ints -> Arrays.stream(ints).mapToObj(id -> strings[id]).collect(Collectors.joining())).forEach(list::add));
        return list;
    }
}
