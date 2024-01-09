package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class ShippingAndPayment{
	private boolean enableDisablePaymentMethod;
	private boolean addRemoveGoogleAPIKey;
	private boolean enableDisable3rdShippingMethod;
	private boolean enableDisableSelfDeliveryMethod;
	private boolean updateSelfDeliveryInformation;
	private boolean update3rdShippingMethodInformation;
	private boolean updatePaymentMethodInformation;
}