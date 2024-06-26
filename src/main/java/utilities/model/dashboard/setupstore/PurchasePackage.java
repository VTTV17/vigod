package utilities.model.dashboard.setupstore;

import lombok.Data;
import utilities.data.DataGenerator;
import utilities.enums.PaymentMethod;
import utilities.enums.newpackage.NewPackage;

@Data
public class PurchasePackage {

	NewPackage newPackage;
	PaymentMethod paymentMethod;

	public void randomPackageAndPaymentMethod (SetupStore store) {
		if (store.getCountry().contentEquals("Vietnam")) {
			newPackage = DataGenerator.getRandomListElement(NewPackage.forVNStore());
			paymentMethod = DataGenerator.getRandomListElement(PaymentMethod.forVNShop());
			return;
		}
		newPackage = DataGenerator.getRandomListElement(NewPackage.forForeignStore());
		paymentMethod = DataGenerator.getRandomListElement(PaymentMethod.forForeignShop());
	}
}