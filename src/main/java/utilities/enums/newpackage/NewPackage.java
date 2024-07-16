package utilities.enums.newpackage;

import java.util.Arrays;
import java.util.List;

public enum NewPackage {
	STARTUP_PLUS("STARTUP+"),
	PROFESSIONAL_PLUS("PROFESSIONAL+"),
	OAO_PLUS("OAO+"),
	ENTERPRISE_PLUS("ENTERPRISE+"),
	BASIC("BASIC"),
	PROFESSIONAL("PROFESSIONAL");
	
    private final String enumValue;

    private NewPackage(String enumValue) {
        this.enumValue = enumValue;
    }

    public static String getValue(NewPackage packageEnum) {
    	return packageEnum.enumValue;
    }
    public static List<NewPackage> forVNStore() {
    	return Arrays.asList(STARTUP_PLUS, PROFESSIONAL_PLUS, OAO_PLUS, ENTERPRISE_PLUS);
    }  
    public static List<NewPackage> forForeignStore() {
    	return Arrays.asList(BASIC, PROFESSIONAL);
    }   
    
    public static NewPackage getKeyFromValue(String value) {
    	return switch (value) {
			case "STARTUP+": yield STARTUP_PLUS;
			case "PROFESSIONAL+": yield PROFESSIONAL_PLUS;
			case "OAO+": yield OAO_PLUS;
			case "ENTERPRISE+": yield ENTERPRISE_PLUS;
			case "BASIC": yield BASIC;
			case "PROFESSIONAL": yield PROFESSIONAL;
			default: throw new IllegalArgumentException("Unexpected value: " + value);
    	};  
    }
    
}
