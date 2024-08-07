package api.Seller.promotion;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIGetBxGyDetail {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIGetBxGyDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BxGyInformation {
        private int id;
        private String name;
        private int storeId;
        private String activeDate;
        private String expiryDate;
        private String createdBy;
        private String createdDate;
        private String lastModifiedDate;
        private String lastModifiedBy;
        private List<Condition> conditions;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Condition {
            private int id;
            private String conditionType;
            private String conditionOption;
            private List<Value> values;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Value {
                private int id;
                private String conditionValue;
                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                private ConditionMetadata conditionMetadata;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class ConditionMetadata {
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private Integer collectionNumItems;
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private String collectionName;
                }
            }
        }
    }

    String getBxGyPath(int storeId, int bxGyId) {
        return "/orderservices2/api/gs-bxgy/%d/%d".formatted(storeId, bxGyId);
    }

    public BxGyInformation getInfo(int bxGyId) {
        return api.get(getBxGyPath(loginInfo.getStoreID(), bxGyId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .as(BxGyInformation.class);
    }
}
