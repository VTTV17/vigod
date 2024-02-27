package utilities.data;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import utilities.utils.jsonFileUtility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utilities.character_limit.CharacterLimit.*;

public class DataGenerator {
    public String generateString(int length) {
        return RandomStringUtils.random(length, true, false);
    }

    public String generateNumber(int length) {
        return RandomStringUtils.random(length, false, true);
    }

    public int generatNumberInBound(int start, int end) {
        Random rand = new Random();
        int random_integer = rand.nextInt(end - start) + start;
        return random_integer;
    }

    /**
     * Returns a list of all the countries in the phoneCodes.json file as Strings.
     * @return a List of all the countries in the phoneCodes.json file
     */
    public List<String> getCountryList() {
        JsonNode data = jsonFileUtility.readJsonFile("phoneCodes.json");
        Iterator<String> it = data.fieldNames();
        List<String> countries = new ArrayList<>();
        while (it.hasNext()) {
            countries.add(it.next());
        }
        return countries;
    }

    /**
     * @return a random country
     */
    public String randomCountry() {
        List<String> countries = getCountryList();
        return countries.get(new Random().nextInt(0, countries.size()));
    }

    /**
     * Returns the phone code for a given country name as a String
     * @param country the name of the country to get the code for
     * @return the phone code for the given country, or null if it is not found
     */
    public String getPhoneCode(String country) {
        JsonNode data = jsonFileUtility.readJsonFile("phoneCodes.json").findValue(country);
        return data.asText();
    }

    /**
     * Returns the country code for a given country name as a String
     * @param country the name of the country to get the code for
     * @return the country code for the given country, or null if it is not found
     */
    public String getCountryCode(String country) {
        JsonNode data = jsonFileUtility.readJsonFile("countryCodes.json").findValue(country);
        return data.asText();
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
            return numbers.remove(0);
        }
    }

    public List<Integer> randomListNumberWithNoDuplicate(int maximum) {
        UniqueRng rng = new UniqueRng(maximum);
        List<Integer> list = new ArrayList<>();
        while (rng.hasNext()) {
            list.add(rng.next());
        }
        return list;
    }

    public List<Integer> randomListNumberCanDuplicate(int maximum) {
        List<Integer> listNumber = new ArrayList<>();
        for (int i = 0; i < maximum; i++) {
            listNumber.add(generatNumberInBound(1, maximum));
        }
        return listNumber;
    }

    /**
     * Generates a random number with the specified number of digits using the current epoch time as a seed.
     * @param numberOfDigits the number of digits in the random number to be generated
     * @return the randomly generated number as a String
     */
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


    public String generateDateTime(String dateFormat, int... plusDate) {
        int plusDay = plusDate.length == 0 ? 0 : plusDate[0];
        return DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.now().plusDays(plusDay));
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
        for (int i = 0; i < numberOfVariationValue.size(); i++) {
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
    public List<String> mixVariationValue(List<String> variationValueList1, List<String> variationValueList2, String language) {
        List<String> mixedVariationValueList = new ArrayList<>();
        for (String var1 : variationValueList1) {
            for (String var2 : variationValueList2) {
                mixedVariationValueList.add("%s|%s_%s".formatted(var1, language, var2));
            }
        }
        return mixedVariationValueList;
    }

    public String getVariationName(Map<String, List<String>> variationMap, String language) {
        // get variation name
        List<String> varName = new ArrayList<>(variationMap.keySet());
        return IntStream.range(1, varName.size()).mapToObj(i -> "|%s_%s".formatted(language, varName.get(i))).collect(Collectors.joining("", "%s_%s".formatted(language, varName.get(0)), ""));
    }

    public List<String> getVariationList(Map<String, List<String>> variationMap, String language) {
        List<List<String>> varValue = new ArrayList<>(variationMap.values());
        List<String> variationList = new ArrayList<>();
        for (String var : varValue.get(0)) {
            variationList.add("%s_%s".formatted(language, var));
        }
        if (varValue.size() > 1) {
            for (int i = 1; i < varValue.size(); i++) {
                variationList = new DataGenerator()
                        .mixVariationValue(variationList, varValue.get(i), language);
            }
        }
        return variationList;
    }

    /**
     * Generates a Vietnamese phone number based on Epoch time
     * @return a {@code String} representing the randomly generated phone number
     */
    public String randomVNPhone() {
        String phone = randomNumberGeneratedFromEpochTime(10);
        String nonZeroDigit = String.valueOf(generatNumberInBound(1, 10));
        if (phone.matches("^0[1-9]\\d+")) {
            return phone;
        }
        if (phone.matches("^(0|[1-9])0\\d+")) {
            return "0" + nonZeroDigit + phone.substring(2);
        }
        if (phone.matches("^[1-9][1-9]\\d+")) {
            return "0" + phone.substring(1);
        }
        return phone;
    }

    /**
     * Generates a foreign phone number based on Epoch time
     * @return a {@code String} representing the randomly generated phone number
     */
    public String randomForeignPhone() {
        String phone = randomNumberGeneratedFromEpochTime(10);
        String nonZeroDigit = String.valueOf(generatNumberInBound(1, 10));
        if (phone.matches("^[1-9]\\d+")) {
            return phone;
        }
        if (phone.matches("^0\\d+")) {
            return nonZeroDigit + phone.substring(1);
        }
        return phone;
    }

    /**
     * Generates a random phone number based on the specified country
     * @param country a {@code String} representing the name of the country
     * @return a {@code String} representing the randomly generated phone number
     */
    public String randomPhoneByCountry(String country) {
        return country.contentEquals("Vietnam") ? randomVNPhone() : randomForeignPhone();
    }

}
