package utilities.data;

import org.apache.commons.lang3.RandomStringUtils;

public class DataGenerator {
    public String generateString(int length) {
        return RandomStringUtils.random(length,true,false);
    }

    public String generateNumber(int length) {
        return RandomStringUtils.random(length, false, true);
    }
}
