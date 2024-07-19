package utilities.enums;

import java.util.Arrays;
import java.util.List;

public enum PaymentMethod {
	BANKTRANSFER,
	ATM,
	VISA,
	PAYPAL;
	
	public static String[] getAllValues() {
		return Arrays.stream(PaymentMethod.values()).map(Enum::name).toArray(String[]::new);
	}
	
	public static List<PaymentMethod> forVNShop() {
		return Arrays.asList(PaymentMethod.values());
	}
	public static List<PaymentMethod> forForeignShop() {
		return Arrays.asList(PAYPAL, BANKTRANSFER);
	}
}
