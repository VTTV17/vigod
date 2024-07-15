package api.Seller.promotion;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class APICreateServiceCampaign {
    LoginDashboardInfo loginInfo;
    API api;
    String CREATE_SERVICE_CAMPAIGN_PATH  = "/orderservices2/api/gs-discount-campaigns/coupons";
    public APICreateServiceCampaign(LoginInformation loginInformation) {
        loginInfo = new Login().getInfo(loginInformation);
        api =  new API();
    }
    public void createServiceDiscountCampaign(int startDatePlus){
        // Get the current local time
        LocalDateTime localDateTime = LocalDateTime.now();

        // Get the time zone of the local time
        ZoneId localZoneId = ZoneId.systemDefault();

        // Get the GMT+0 time zone
        ZoneId gmtZoneId = ZoneId.of("GMT+0");

        // start date
        Instant productDiscountCampaignStartTime = localDateTime.truncatedTo(ChronoUnit.DAYS).atZone(localZoneId).withZoneSameInstant(gmtZoneId).plusDays(startDatePlus).toInstant();

        // end date
        Instant productDiscountCampaignEndTime = localDateTime.truncatedTo(ChronoUnit.DAYS).atZone(localZoneId).withZoneSameInstant(gmtZoneId).plusDays(startDatePlus).plus(Duration.ofHours(23).plusMinutes(59)).toInstant();

        String body = """
                {
                  "description": "",
                  "discounts": [
                    {
                      "couponCode": "unused_code",
                      "activeDate": "%s",
                      "conditions": [
                        {
                          "conditionOption": "CUSTOMER_SEGMENT_ALL_CUSTOMERS",
                          "conditionType": "CUSTOMER_SEGMENT",
                          "values": []
                        },
                        {
                          "conditionOption": "APPLIES_TO_ALL_SERVICES",
                          "conditionType": "APPLIES_TO",
                          "values": []
                        },
                        {
                          "conditionOption": "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS",
                          "conditionType": "MINIMUM_REQUIREMENTS",
                          "values": [
                            {
                              "conditionValue": 1
                            }
                          ]
                        },
                        {
                          "conditionOption": "APPLIES_TO_BRANCH_ALL_BRANCHES",
                          "conditionType": "APPLIES_TO_BRANCH",
                          "values": []
                        },
                        {
                          "conditionType": "PLATFORMS",
                          "values": [
                            {
                              "conditionValue": "WEB"
                            },
                            {
                              "conditionValue": "APP"
                            },
                            {
                              "conditionValue": "INSTORE"
                            },
                            {
                              "conditionValue": "SOCIAL"
                            }
                          ]
                        }
                      ],
                      "couponType": "PERCENTAGE",
                      "couponValue": "10",
                      "expiredDate": "%s",
                      "storeId": "%s",
                      "type": "WHOLE_SALE_SERVICE"
                    }
                  ],
                  "name": "%s",
                  "storeId": "%s",
                  "timeCopy": 0
                }
                """.formatted(productDiscountCampaignStartTime,productDiscountCampaignEndTime,loginInfo.getStoreID(),"Service campaign"+localDateTime,loginInfo.getStoreID());
        Response response = api.post(CREATE_SERVICE_CAMPAIGN_PATH,loginInfo.getAccessToken(),body);
        response.then().statusCode(200);
    }
}
