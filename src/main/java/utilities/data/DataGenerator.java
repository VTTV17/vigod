package utilities.data;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

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
}
