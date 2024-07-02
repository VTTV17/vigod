package utilities.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifmif.common.regex.Generex;

import api.dotrand.DotrandAPI;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;

import utilities.model.dashboard.setupstore.CountryData;
import utilities.utils.jsonFileUtility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
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

    public long generateLongNumber(long max) {
        Random rand = new Random();
        return rand.nextLong(max);
    }

    public int generatNumberInBound(int start, int end) {
        Random rand = new Random();
        int random_integer = rand.nextInt(end - start) + start;
        return random_integer;
    }

    /**
     * Returns a list of all the countries in the CountryCodes.json file as Strings.
     * @return a List of all the countries in the CountryCodes.json file
     */
    public List<String> getCountryList() {
        JsonNode data = jsonFileUtility.readJsonFile("CountryCodes.json");
        Iterator<String> it = data.fieldNames();
        List<String> countries = new ArrayList<>();
        while (it.hasNext()) {
            countries.add(it.next());
        }
        return countries;
    }
    
    //Will remove later
    public static List<CountryData> getCountryListExp() {
    	JsonNode data = jsonFileUtility.readJsonFile("CountryEntity.json");
    	try {
			return JsonPath.from(new ObjectMapper().writeValueAsString(data)).getList(".", CountryData.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return null;
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
        JsonNode data = jsonFileUtility.readJsonFile("CountryCodes.json").findValue(country).findValue("phoneCode");
        return data.asText();
    }

    /**
     * Returns the country code for a given country name as a String
     * @param country the name of the country to get the code for
     * @return the country code for the given country, or null if it is not found
     */
    public String getCountryCode(String country) {
        JsonNode data = jsonFileUtility.readJsonFile("CountryCodes.json").findValue(country).findValue("countryCode");
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

	public static String randomValidPhoneByCountry(String countryCode) {
		String phone = "";
		String regex = DotrandAPI.getPhoneRegexJsonPath(countryCode);
		for (int i=0; i<1000; i++) { //At times the function returns phone numbers with the wrong format, so we repeat it several times until a valid phone is returned
			phone = new Generex(regex).random();
			if (phone.matches("\\d+")) break; 
		}
		return phone;
	}	    
    
    public static <T> T getRandomListElement(List<T> list) {
        return list.get(new Random().nextInt(0, list.size()));
    }

    public String getCurrentDate(String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    @SneakyThrows
    public String getFilePath(String fileName) {
        File root = new File(System.getProperty("user.dir"));
        List<Path> paths = Files.walk(Paths.get(root.toString())).toList();
        Optional<Path> filePath = paths.stream()
                .filter(path1 -> !Files.isDirectory(path1))
                .filter(path -> path.getFileName().toString().equals(fileName))
                .findFirst();
        return filePath.map(Path::toString).orElse("");
    }

    @SneakyThrows
    public String getFolderPath(String folderName) {
        File root = new File(System.getProperty("user.dir"));
        List<Path> paths = Files.walk(Paths.get(root.toString())).toList();
        Optional<Path> folderPath = paths.stream()
                .filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().equals(folderName))
                .findFirst();
        return folderPath.map(Path::toString).orElse("");
    }


    public static String getFirstString(String... strings) {
        return Optional.ofNullable(strings).filter(stringArr -> stringArr.length > 0).map(stringArr -> stringArr[0]).orElse("");
    }

    public static String getStringByRegex(String inputString, String regex) {
        return Pattern.compile(regex).matcher(inputString)
                .results()
                .map(matchResult -> matchResult.group(1)).findFirst().orElse(null);
    }

    public static List<String> getListStringByRegex(String inputString, String regex) {
        return Pattern.compile(regex).matcher(inputString)
                .results()
                .map(matchResult -> matchResult.group(1))
                .toList();
    }

    public String generatePreviousTerm(String format) {
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat(format);
        return dt.format(getPreviousTerm(date));
    }

    public static Date getPreviousTerm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.MONTH) == Calendar.JANUARY) {
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
        } else {
            calendar.roll(Calendar.MONTH, false);
        }
        calendar.set(Calendar.DATE, 01);
        return calendar.getTime();
    }
}
