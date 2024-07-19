package utilities.model.dashboard.setupstore;

import java.util.Random;

import lombok.Data;
import utilities.data.DataGenerator;
import utilities.enums.PaymentMethod;
import utilities.enums.newpackage.NewPackage;

@Data
public class PurchasePlanDG {

	NewPackage newPackage;
	PaymentMethod paymentMethod;
	int period;

	public void randomPackageAndPaymentMethod (SetupStoreDG store) {
		if (store.getCountry().contentEquals("Vietnam")) {
			newPackage = DataGenerator.getRandomListElement(NewPackage.forVNStore());
			paymentMethod = DataGenerator.getRandomListElement(PaymentMethod.forVNShop());
		} else {
			newPackage = DataGenerator.getRandomListElement(NewPackage.forForeignStore());
			paymentMethod = DataGenerator.getRandomListElement(PaymentMethod.forForeignShop());
		}
		period = store.getDomain().contains("biz") ? 1 : new Random().nextInt(1, 4);
	}
}