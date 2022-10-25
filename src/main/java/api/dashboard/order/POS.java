package api.dashboard.order;

import utilities.api.API;

import java.util.Collections;

import static api.dashboard.customers.Customers.*;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static api.dashboard.products.CreateProduct.*;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class POS {
    String CREATE_POS_ORDER_PATH = "/orderservices2/api/gs/checkout/instore/v2";

    public void createPOSOrderNoDelivery() {
        String modelId = isVariation ? variationModelID.get(variationSellingPrice.indexOf(Collections.max(variationSellingPrice))).toString() : "";
        int quantity = isVariation ? nextInt(variationStockQuantity.get(variationSellingPrice.indexOf(Collections.max(variationSellingPrice)))) + 1 : nextInt(withoutVariationStock) + 1;
        StringBuilder imeiSerial = new StringBuilder();
        if (isIMEIProduct) {
            for (int i = 0; i < quantity; i++) {
                imeiSerial.append("\"%s\"".formatted(isVariation ? "%s%s_IMEI_%s".formatted(variationList.get(variationSellingPrice.indexOf(Collections.max(variationSellingPrice))), branchName.get(0), i)
                        : "%s_IMEI_%s".formatted(branchName.get(0), i)));
                imeiSerial.append(i == quantity - 1 ? "" : ",");
            }
        }
        String body = """
                {
                    "buyerId": "%s",
                    "profileId": %s,
                    "guest": false,
                    "cartItemVMs": [
                        {
                            "itemId": "%s",
                            "modelId": "%s",
                            "quantity": "%s",
                            "imeiSerial": [ %s ],
                            "branchId": %s
                        }
                    ],
                    "checkNoDimension": false,
                    "deliveryInfo": {
                        "contactName": "%s",
                        "phoneNumber": "%s",
                        "address": "",
                        "address2": "",
                        "wardCode": "",
                        "districtCode": "",
                        "locationCode": "",
                        "countryCode": "VN",
                        "city": "",
                        "zipCode": ""
                    },
                    "deliveryServiceId": 75,
                    "langKey": "en",
                    "sellerNote": "",
                    "note": "",
                    "paymentCode": "",
                    "paymentMethod": "CASH",
                    "storeId": "%s",
                    "platform": "WEB",
                    "weight": 0,
                    "width": 0,
                    "length": 0,
                    "height": 0,
                    "selfDeliveryFee": 0,
                    "coupons": [],
                    "selfDeliveryFeeDiscount": null,
                    "branchId": %s,
                    "usePoint": null,
                    "receivedAmount": 0,
                    "directDiscount": null,
                    "customerId": %s
                }""".formatted(buyerId, profileId, productID, modelId, quantity, imeiSerial, branchIDList.get(0), customerName, customerPhone, storeID, branchIDList.get(0), profileId);
        new API().post(CREATE_POS_ORDER_PATH, accessToken, body).prettyPrint();
    }
}
