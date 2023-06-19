package utilities.enums;

import java.util.Arrays;

public enum PaymentMethod {
	BANKTRANSFER,
	ATM,
	VISA,
	PAYPAL;
	
	public static String[] getAllValues() {
		return Arrays.stream(PaymentMethod.values()).map(Enum::name).toArray(String[]::new);
	}
}
