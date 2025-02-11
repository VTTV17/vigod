package utilities.utils;

import java.util.List;
import java.util.Random;

public class ListUtils {

	/**
	 * Gets a random element from the list
	 * @param <T>
	 * @param list
	 */
    public static <T> T getRandomListElement(List<T> list) {
        return list.get(new Random().nextInt(0, list.size()));
    }
}
