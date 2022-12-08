package utilities.data;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utilities.character_limit.CharacterLimit.*;
import static utilities.character_limit.CharacterLimit.MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION;

public class DataGenerator {
    public String generateString(int length) {
        return RandomStringUtils.random(length,true,false);
    }

    public String generateNumber(int length) {
        return RandomStringUtils.random(length, false, true);
    }
    public int generatNumberInBound( int start, int end){
        Random rand = new Random();
        int random_integer = rand.nextInt(end-start) + start;
        return random_integer;
    }
    public static class UniqueRng implements Iterator<Integer> {
        private List<Integer> numbers = new ArrayList<>();

        public UniqueRng(int n) {
            for (int i = 1; i <= n; i++) {
                numbers.add(i);
            }

            Collections.shuffle(numbers);
        }

        @Override
        public boolean hasNext() {
            return !numbers.isEmpty();
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return numbers.remove(0);        }
    }
    public List<Integer> randomListNumberWithNoDulicate (int maximum){
        UniqueRng rng = new UniqueRng(maximum);
        List<Integer> list = new ArrayList<>();
        while (rng.hasNext()) {
           list.add(rng.next());
        }
        return list;
    }

    public String generateDateTime(String dateFormat) {
        return DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.now());
    }

    /**
     * generate Variation value
     */
    private List<String> generateListString(int index, int size) {
        List<String> randomList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            randomList.add("var%s_%s".formatted(index, i + 1));
        }
        return randomList;
    }

    /**
     * generate variation maps (variation name : get variation value)
     */
    public Map<String, List<String>> randomVariationMap() {
        // init variation map
        // key: variation name
        // values: get of variation name
        Map<String, List<String>> map = new HashMap<>();

        // generate number of variation
        int variationNum = RandomUtils.nextInt(MAX_VARIATION_QUANTITY) + 1;

        // init get number of each variation
        List<Integer> numberOfVariationValue = new ArrayList<>();

        // generate number variation value of first variation
        numberOfVariationValue.add(RandomUtils.nextInt(MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION) + 1);

        // get number variation value of other variation
        for (int i = 1; i < variationNum; i++) {
            int prevMulti = 1;
            for (int id = 0; id < i; id++) {
                prevMulti = prevMulti * numberOfVariationValue.get(id);
            }
            numberOfVariationValue.add(RandomUtils.nextInt(Math.min((MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS / prevMulti), MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION)) + 1);
        }

        // generate random data for variation map
        for (int i = 0; i < numberOfVariationValue.size(); i ++) {
            map.put("var%s".formatted(i + 1), generateListString(i + 1, numberOfVariationValue.get(i)));
        }

        // return variation map
        return new TreeMap<>(map);
    }

    /**
     * <p> get get variation value after mixed variation</p>
     * <p> example: var1 = {a, b, c} and var2 = {d}</p>
     * <p> with above variations, we have 3 variation value {a|d, b|d, c|d}</p>
     */
    public List<String> mixVariationValue(List<String> variationValueList1, List<String> variationValueList2) {
        List<String> mixedVariationValueList = new ArrayList<>();
        for (String var1 : variationValueList1) {
            for (String var2 : variationValueList2) {
                mixedVariationValueList.add(var1 + "|" + var2);
            }
        }
        return mixedVariationValueList;
    }
}
