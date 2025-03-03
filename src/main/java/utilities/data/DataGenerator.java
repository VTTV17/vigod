package utilities.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifmif.common.regex.Generex;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;

import utilities.constant.Constant;
import utilities.enums.newpackage.NewPackage;
import utilities.model.dashboard.setting.plan.PlanNameAndPrice;
import utilities.model.dashboard.setupstore.CountryData;
import utilities.utils.jsonFileUtility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
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

    public static int generatNumberInBound(int start, int end) {
        Random rand = new Random();
        int random_integer = rand.nextInt(end - start) + start;
        return random_integer;
    }
    public static double generatNumberInBound(double start, double end) {
        Random rand = new Random();
        double randomValue = start + (end - start) * rand.nextDouble();;
        return randomValue;
    }
    /**
     * Returns a list of all the countries in the CountryCodes.json file as Strings.
     * @return a List of all the countries in the CountryCodes.json file
     */
    public static List<String> getCountryList() {
        JsonNode data = jsonFileUtility.readJsonFile("CountryCodes.json");
        Iterator<String> it = data.fieldNames();
        List<String> countries = new ArrayList<>();
        while (it.hasNext()) {
            countries.add(it.next());
        }
        return countries;
    }

	/**
	 * Retrieves plan benefits from a json file
	 * @param country
	 * @param period 1/2/3
	 * @param lang vi/en
	 * @return
	 */
	public static List<List<String>> getBenefitsByPlan(String country, int period, String lang){
		/**
		 * Creation of shops located in Vietnam is not supported for domain .biz
		 * So country field will be adequately useful to decide what packages are displayed
		 */
		String benefitJsonFile = country.contentEquals("Vietnam") ? "PlanPerksVN.json" : "PlanPerksBIZ.json";
		String duration = switch (period) {
		case 1 -> "ONE_YEAR";
		case 2 -> "TWO_YEAR";
		case 3 -> "THREE_YEAR";
		default -> throw new IllegalArgumentException("%s is invalid. Please try again with a different value".formatted(period));
		};
		
		JsonPath jsonPath = JsonPath.from(jsonFileUtility.readFileToString(benefitJsonFile));
		List<List<String>> benefits = jsonPath.getList("%s.%s".formatted(duration, lang)); 
		return benefits;
	}  
	
    /**
     * @return a random country
     */
    public static String randomCountry() {
        List<String> countries = getCountryList();
        return countries.get(new Random().nextInt(0, countries.size()));
    }

    /**
     * Returns the phone code for a given country name as a String
     * @param country the name of the country to get the code for
     * @return the phone code for the given country, or null if it is not found
     */
    public static String getPhoneCode(String country) {
        JsonNode data = jsonFileUtility.readJsonFile("CountryCodes.json").findValue(country).findValue("phoneCode");
        return "+%s".formatted(data.asText());
    }

    /**
     * Returns the country code for a given country name as a String
     * @param country the name of the country to get the code for
     * @return the country code for the given country, or null if it is not found
     */
    public static String getCountryCode(String country) {
        JsonNode data = jsonFileUtility.readJsonFile("CountryCodes.json").findValue(country).findValue("countryCode");
        return data.asText();
    }
    
    /**
     * Returns the phone regex for a given country name as a String
     * @param country the name of the country to get the phone regex for
     * @return the phone regex for the given country, or null if it is not found
     */
    public static String getPhoneRegex(String country) {
    	JsonNode data = jsonFileUtility.readJsonFile("CountryCodes.json").findValue(country).findValue("phoneRegex");
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
    public static PlanNameAndPrice getPlanInfo(NewPackage packagePlan) {
        JsonNode data = jsonFileUtility.readJsonFile("PackagePrice.json");
        try {
            return JsonPath.from(new ObjectMapper().writeValueAsString(data)).getObject(packagePlan.name(), PlanNameAndPrice.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
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
    static public String randomNumberGeneratedFromEpochTime(int numberOfDigits) {
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
    private List<String> generateListString(String defaultLanguage, int index, int size) {
        return IntStream.range(0, size).mapToObj(i -> "%s_var%s_%s".formatted(defaultLanguage, index, i + 1)).toList();
    }

    private List<String> generateListStringWithoutLangKey(int index, int size) {
        return IntStream.range(0, size).mapToObj(i -> "var%s_%s".formatted(index, i + 1)).toList();
    }

    public List<Integer> getNumOfValuesOnEachGroup(int numberOfVariations, int numberOfGroups) {
        if (numberOfGroups == 1) return List.of(numberOfVariations);
        int factor = IntStream.range(2, numberOfVariations).filter(i -> numberOfVariations % i == 0).findFirst().orElse(1);
        return List.of(factor, numberOfVariations / factor);
    }
    /**
     * generate variation maps (variation name : get variation value)
     */
    public Map<String, List<String>> randomVariationMap(String defaultLanguage) {
        // generate number of variation groups
        int numberOfGroups = nextInt(MAX_VARIATION_QUANTITY) + 1;

        // init number of variation values
        int numberOfVariations = nextInt((numberOfGroups == 1) ? MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION : MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS) + 1;

        // get number of value of each group variation
        List<Integer> numberOfVariationValue = getNumOfValuesOnEachGroup(numberOfVariations, numberOfGroups);

        // generate random data for variation map
        return new TreeMap<>(IntStream.range(0, numberOfVariationValue.size())
                .boxed()
                .collect(Collectors.toMap(valueIndex -> "%s_var%s".formatted(defaultLanguage, valueIndex + 1),
                        valueIndex -> generateListString(defaultLanguage, valueIndex + 1, numberOfVariationValue.get(valueIndex)),
                        (a, b) -> b)));
    }


    /**
     * generate variation maps (variation name : get variation value)
     */
    public Map<String, List<String>> randomVariationMap(int variationNum, String defaultLanguage) {
        // generate number of variation groups
        int numberOfGroups = nextInt(MAX_VARIATION_QUANTITY) + 1;

        // get number of value of each group variation
        List<Integer> numberOfVariationValue = getNumOfValuesOnEachGroup(variationNum, numberOfGroups);

        // generate random data for variation map
        return new TreeMap<>(IntStream.range(0, numberOfVariationValue.size())
                .boxed()
                .collect(Collectors.toMap(valueIndex -> "%s_var%s".formatted(defaultLanguage, valueIndex + 1),
                        valueIndex -> generateListString(defaultLanguage, valueIndex + 1, numberOfVariationValue.get(valueIndex)),
                        (a, b) -> b)));
    }

    public Map<String, List<String>> randomVariationMapWithoutLangKey(int variationNum) {
        // generate number of variation groups
        int numberOfGroups = nextInt(MAX_VARIATION_QUANTITY) + 1;

        // get number of value of each group variation
        List<Integer> numberOfVariationValue = getNumOfValuesOnEachGroup(variationNum, numberOfGroups);

        // generate random data for variation map
        return new TreeMap<>(IntStream.range(0, numberOfVariationValue.size())
                .boxed()
                .collect(Collectors.toMap(valueIndex -> "var%s".formatted(valueIndex + 1),
                        valueIndex -> generateListStringWithoutLangKey(valueIndex + 1, numberOfVariationValue.get(valueIndex)),
                        (a, b) -> b)));
    }

    /**
     * <p> get get variation value after mixed variation</p>
     * <p> example: var1 = {a, b, c} and var2 = {d}</p>
     * <p> with above variations, we have 3 variation value {a|d, b|d, c|d}</p>
     */
    public List<String> mixVariationValue(List<String> variationValueList1, List<String> variationValueList2) {
        List<String> mixedVariationValueList = new ArrayList<>();
        variationValueList1.forEach(var1 -> variationValueList2.stream().map(var2 -> "%s|%s".formatted(var1, var2)).forEach(mixedVariationValueList::add));
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
            for (int valueIndex = 1; valueIndex < varValue.size(); valueIndex++) {
                variationList = new DataGenerator()
                        .mixVariationValue(variationList, varValue.get(valueIndex));
            }
        }
        return variationList;
    }

    public List<String> getVariationList(Map<String, List<String>> variationMap) {
        List<List<String>> varValue = new ArrayList<>(variationMap.values());
        List<String> variationList = new ArrayList<>(varValue.get(0));
        if (varValue.size() > 1) {
            for (int valueIndex = 1; valueIndex < varValue.size(); valueIndex++) {
                variationList = new DataGenerator()
                        .mixVariationValue(variationList, varValue.get(valueIndex));
            }
        }
        return variationList;
    }

    /**
     * Generates a Vietnamese phone number based on Epoch time
     * @return a {@code String} representing the randomly generated phone number
     */
    static public String randomVNPhone() {
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
    static public String randomForeignPhone() {
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
    static public String randomPhoneByCountry(String country) {
        return country.contentEquals("Vietnam") ? randomVNPhone() : randomForeignPhone();
    }
    
    public static String generatePhoneFromRegex(String regex) {
    	String phone = "";
    	for (int i=0; i<1000; i++) { //At times the function returns phone numbers with the wrong format, so we repeat it several times until a valid phone is returned
    		phone = new Generex(regex).random();
    		if (phone.matches("\\d+")) break; 
    	}
    	return phone;
    }	    

	public static String randomValidPhoneByCountry(String country) {
		return generatePhoneFromRegex(getPhoneRegex(country));
	}	    
    
    public static String getCurrentDate(String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    @SneakyThrows
    public String getPathOfFileInResourcesRoot(String fileName) {
        return getPathOfFile("resources", fileName);
    }

    @SneakyThrows
    public String getPathOfFile(String rootFolder, String fileName) {
        // Start walking from the user's current directory
        Path root = Paths.get(System.getProperty("user.dir"));

        // Find the file matching the criteria
        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(path -> !Files.isDirectory(path)) // Exclude directories
                    .filter(path -> path.toString().contains(rootFolder)) // Check root folder
                    .filter(path -> path.getFileName().toString().equals(fileName)) // Check file name
                    .map(Path::toString) // Convert Path to String
                    .findFirst() // Get the first matching path
                    .orElse(""); // Return empty string if not found
        }
    }

    @SneakyThrows
    public String getPathOfFolder(String folderName) {
        return getPathOfFolder(null, folderName);
    }

    @SneakyThrows
    public String getPathOfFolder(String rootFolder, String folderName) {
        // Start walking from the user's current directory
        Path root = Paths.get(System.getProperty("user.dir"));

        // Find the folder matching the criteria
        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isDirectory) // Only consider directories
                    .filter(path -> {
                        boolean matchesName = path.getFileName().toString().equals(folderName);
                        boolean matchesRoot = rootFolder == null || path.toString().contains(rootFolder);
                        return matchesName && matchesRoot;
                    }) // Match folder name and optional root folder
                    .map(Path::toString) // Convert Path to String
                    .findFirst() // Get the first match
                    .orElse(""); // Return an empty string if not found
        }
    }

    public String getPathOfFolderInResourceRoot(String folderName) {
        return getPathOfFolder("resources", folderName);
    }

    public List<String> getAllFileNamesInFolder(String folderName) {
        File root = new File(getPathOfFolder(folderName));
        return Arrays.stream(Objects.requireNonNull(root.listFiles())).filter(File::isFile).map(File::getName).toList();
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

    public enum TimeUnits {
        SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS;
    }
    public static LocalDateTime forwardTime(LocalDateTime initialTime, long timeAmount, TimeUnits unit) {
        return switch (unit) {
            case SECONDS ->  initialTime.plusSeconds(timeAmount);
            case MINUTES ->  initialTime.plusMinutes(timeAmount);
            case HOURS ->  initialTime.plusHours(timeAmount);
            case DAYS ->  initialTime.plusDays(timeAmount);
            case WEEKS ->  initialTime.plusWeeks(timeAmount);
            case MONTHS ->  initialTime.plusMonths(timeAmount);
            case YEARS ->  initialTime.plusYears(timeAmount);
            default -> throw new IllegalArgumentException("Unexpected value: " + unit);
        };
    }
    public static LocalDateTime forwardTime(long timeAmount, TimeUnits unit) {
        LocalDateTime currentTime = LocalDateTime.now();
        return forwardTime(currentTime, timeAmount, unit);
    }
    public static String forwardTimeWithFormat(LocalDateTime initialTime, long timeAmount, TimeUnits unit, String outputFormat) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(outputFormat);
        return dtf.format(forwardTime(initialTime, timeAmount, unit));
    }
    public static String forwardTimeWithFormat(long timeAmount, TimeUnits unit, String outputFormat) {
        LocalDateTime currentTime = LocalDateTime.now();
        return forwardTimeWithFormat(currentTime, timeAmount, unit, outputFormat);
    }
    
	/**
	 * Extract numbers from a string
	 * @param rawAmount
	 */
	public static String extractDigits(String rawAmount) {
		return rawAmount.replaceAll("[^\\d+\\.-]","");
	}
    public static String getDateByTimeZone(String timeZone, String utcDate){
        if(utcDate==null) return null;
        // Parse the text to a ZonedDateTime in UTC
        ZonedDateTime utcDateTime = ZonedDateTime.parse(utcDate, DateTimeFormatter.ISO_DATE_TIME);

        // Convert to timezone
        ZonedDateTime utcPlusTimeZoneDateTime = utcDateTime.withZoneSameInstant(ZoneId.of(timeZone));

        // Extract only the date part
        LocalDate utcPlusTimeZoneDate = utcPlusTimeZoneDateTime.toLocalDate();

        return utcPlusTimeZoneDate.toString();
    }

    /**
     * Generates a random valid password. Eg "password12#"
     */
    static public String randomValidPassword() {
    	return new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random();
    }
    
    /**
     * Generates a fake email address. Eg "cdfgg12345@hmail.cdf"
     */
    static public String randomCorrectFormatEmail() {
    	return new Generex("[a-z]{5}\\d{5}\\@[a-z]mail\\.[a-z]{2,3}").random();
    }
    
    /**
     * Generates a phone number ranging from 8 to 15 digits. Eg "12345678"
     */
    static public String randomPhone() {
    	return generatePhoneFromRegex("\\d{8,15}");
    }
    
    /**
     * <p>Generates a random money amount based on the provided currency symbol.
     * @param currencySymbol  Eg. đ, $
     * @return Eg. 34000 or 34.34
     */
    static public String randomMoneyAmount(String currencySymbol) {
		if (currencySymbol.contentEquals(Constant.VND_SYMBOL)) {
			return new Generex("[1-9]\\d{2,5}").random();
		}
		return new Generex("[1-9]\\d{0,2}\\.\\d{2}").random();
    }
    
}
