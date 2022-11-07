package utilities.data;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
	public String randomNumberGeneratedFromEpochTime(int numberOfDigits) {
		long time = System.currentTimeMillis();
		System.out.println("Current Epoch time is: " + time);
		Matcher m = Pattern.compile("\\d{%d}$".formatted(numberOfDigits)).matcher(String.valueOf(time));
		String randomNumber = null;
		if (m.find()) {
			randomNumber = m.group();
		}	
		System.out.println("Random number generated is: " + randomNumber);
		return randomNumber;
	}
    
}
