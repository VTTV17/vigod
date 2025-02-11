package utilities.model.dashboard.setupstore;

import java.util.Random;

import lombok.Data;
import utilities.enums.Domain;
import utilities.enums.PaymentMethod;
import utilities.enums.newpackage.NewPackage;
import utilities.utils.ListUtils;

@Data
public class PurchasePlanDG {

	NewPackage newPackage;
	PaymentMethod paymentMethod;
	int period;

	public void randomPackageAndPaymentMethod (SetupStoreDG store) {
		period = store.getDomain().equals(Domain.BIZ) ? 1 : new Random().nextInt(1, 4);
		
		if (store.getCountry().contentEquals("Vietnam")) {
			newPackage = ListUtils.getRandomListElement(NewPackage.forVNStore());
			paymentMethod = ListUtils.getRandomListElement(PaymentMethod.forVNShop());
		} else {
			newPackage = ListUtils.getRandomListElement(NewPackage.forForeignStore());
			paymentMethod = ListUtils.getRandomListElement(PaymentMethod.forForeignShop());
		}
	}
}