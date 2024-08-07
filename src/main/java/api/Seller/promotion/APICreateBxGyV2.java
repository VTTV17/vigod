package api.Seller.promotion;

import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.products.product_collections.APIProductCollection;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

public class APICreateBxGyV2 {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String createBxGyPath = "/orderservices2/api/gs-bxgy";

    public APICreateBxGyV2(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    @AllArgsConstructor
    public static class BxGyPayload {
        private String name;
        private String activeDate;
        private String expiryDate;
        private int storeId;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer usageLimit;
        private List<Condition> conditions;
    }

    @Data
    @AllArgsConstructor
    public static class Condition {
        private Object conditionType;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object conditionOption;
        private List<ConditionValue> values;
    }

    @Data
    @AllArgsConstructor
    public static class ConditionValue {
        private Object conditionValue;
    }

    private enum BxGyConditionType {
        CUSTOMER_SEGMENT, GIFT_GIVEAWAY, APPLIED_BRANCHES, PLATFORM, DISCOUNT_METHOD, APPLIES_TO
    }

    private enum CustomerSegment {
        ALL_CUSTOMERS, SPECIFIC_SEGMENT
    }

    private enum GiveAway {
        PRODUCT_COLLECTIONS, SPECIFIC_PRODUCTS
    }

    private enum AppliedBranches {
        ALL_BRANCHES, SPECIFIC_BRANCH
    }

    private enum DiscountMethod {
        FREE, DISCOUNT_FIX_AMOUNT, DISCOUNT_PERCENTAGE, GIVE_AWAY_MAXIMUM_QUANTITY
    }

    private enum AppliesTo {
        ANY_ITEMS_SPECIFIC_PRODUCT, ANY_ITEMS_MINIMUM_QUANTITY, COMBO
    }

    private enum Platform {
        WEB, APP, INSTORE, GOSOCIAL;

        public static List<Platform> getAllPlatforms() {
            return Arrays.asList(Platform.values());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BxGyPrecondition {
        private boolean isComboBxGy;
        private List<Integer> segmentIds = new ArrayList<>();
        private List<Integer> giveAwayCollectionIds = new ArrayList<>();
        private List<Integer> giveAwayProductIds = new ArrayList<>();
        private List<Integer> appliedBranchIds = new ArrayList<>();
        private Long discountFixAmount;
        private Integer discountPercentage;
        private long giveAwayMaximumQuantity;
        private List<Integer> appliesToProductIds;
        private long appliesToProductMinimumQuantity;
        private String activeDate;
        private String expiredDate;
        private Integer usageLimit;
    }

    private Condition getCustomerCondition(BxGyPrecondition precondition) {
        return precondition.getSegmentIds().isEmpty()
                ? new Condition(BxGyConditionType.CUSTOMER_SEGMENT,
                CustomerSegment.ALL_CUSTOMERS,
                List.of())
                : new Condition(BxGyConditionType.CUSTOMER_SEGMENT,
                CustomerSegment.SPECIFIC_SEGMENT,
                List.of(new ConditionValue(new APISegment(loginInformation).getSegmentList().get(0).getId())));
    }

    private Condition getGiftGiveAwayCondition(BxGyPrecondition precondition) {
        return precondition.getGiveAwayCollectionIds().isEmpty()
                ? new Condition(BxGyConditionType.GIFT_GIVEAWAY,
                GiveAway.SPECIFIC_PRODUCTS,
                precondition.getGiveAwayProductIds()
                        .parallelStream()
                        .map(productId -> new ConditionValue(new APIProductDetailV2(loginInformation).getInfo(productId).isHasModel()
                                ? productId + "-ALL"
                                : productId))
                        .toList())
                : new Condition(BxGyConditionType.GIFT_GIVEAWAY,
                GiveAway.PRODUCT_COLLECTIONS,
                precondition.getGiveAwayCollectionIds().parallelStream().map(ConditionValue::new).toList());
    }

    private Condition getAppliedBranchesCondition(BxGyPrecondition precondition) {
        return precondition.getAppliedBranchIds().isEmpty() ? new Condition(BxGyConditionType.APPLIED_BRANCHES,
                AppliedBranches.ALL_BRANCHES,
                List.of()) : new Condition(BxGyConditionType.APPLIED_BRANCHES,
                AppliedBranches.SPECIFIC_BRANCH,
                List.of(new ConditionValue(loginInfo.getAssignedBranchesIds().get(0))));
    }

    private Condition getPlatformCondition() {
        return new Condition(BxGyConditionType.PLATFORM,
                null,
                Platform.getAllPlatforms().parallelStream().map(ConditionValue::new).toList());
    }

    private List<Condition> getDiscountMethod(BxGyPrecondition precondition) {
        // Init discount method conditions
        List<Condition> conditions = new ArrayList<>();
        // Check discount method is FREE, DISCOUNT_FIX_AMOUNT or DISCOUNT_PERCENTAGE
        if (precondition.getDiscountFixAmount() != null) {
            conditions.add(new Condition(BxGyConditionType.DISCOUNT_METHOD,
                    DiscountMethod.DISCOUNT_FIX_AMOUNT,
                    List.of(new ConditionValue(precondition.getDiscountFixAmount()))));
        } else if (precondition.getDiscountPercentage() != null) {
            conditions.add(new Condition(BxGyConditionType.DISCOUNT_METHOD,
                    DiscountMethod.DISCOUNT_PERCENTAGE,
                    List.of(new ConditionValue(precondition.getDiscountPercentage()))));
        } else {
            conditions.add(new Condition(BxGyConditionType.DISCOUNT_METHOD,
                    DiscountMethod.FREE,
                    List.of()));
        }

        // Get maximum give away quantity
        conditions.add(new Condition(BxGyConditionType.DISCOUNT_METHOD,
                DiscountMethod.GIVE_AWAY_MAXIMUM_QUANTITY,
                List.of(new ConditionValue(precondition.getGiveAwayMaximumQuantity()))));

        // return discount method conditions
        return conditions;
    }

    private List<Condition> getAppliesToCondition(BxGyPrecondition precondition) {
        // Init applies to conditions
        List<Condition> conditions = new ArrayList<>();

        // Check promotion is combo or not
        if (precondition.isComboBxGy) {
            conditions.add(new Condition(BxGyConditionType.APPLIES_TO,
                    AppliesTo.COMBO,
                    precondition.getAppliesToProductIds()
                            .parallelStream()
                            .map(productId -> new ConditionValue(new APIProductDetailV2(loginInformation).getInfo(productId).isHasModel()
                                    ? (productId + "-ALL|" + precondition.getAppliesToProductMinimumQuantity())
                                    : (productId + "|" + precondition.getAppliesToProductMinimumQuantity())))
                            .toList()));
        } else {
            // Add any items minimum quantity
            conditions.add(new Condition(BxGyConditionType.APPLIES_TO,
                    AppliesTo.ANY_ITEMS_MINIMUM_QUANTITY,
                    List.of(new ConditionValue(precondition.getAppliesToProductMinimumQuantity()))));

            // Add any items specific product
            conditions.add(new Condition(BxGyConditionType.APPLIES_TO,
                    AppliesTo.COMBO,
                    precondition.getAppliesToProductIds()
                            .parallelStream()
                            .map(productId -> new ConditionValue(new APIProductDetailV2(loginInformation).getInfo(productId).isHasModel()
                                    ? (productId + "-ALL")
                                    : productId))
                            .toList()));
        }

        // return applies to conditions
        return conditions;
    }

    public BxGyPayload getBxGyPayload(BxGyPrecondition precondition) {
        // Get list conditions
        List<Condition> conditions = new ArrayList<>();
        conditions.add(getCustomerCondition(precondition));
        conditions.add(getGiftGiveAwayCondition(precondition));
        conditions.add(getAppliedBranchesCondition(precondition));
        conditions.add(getPlatformCondition());
        conditions.addAll(getDiscountMethod(precondition));
        conditions.addAll(getAppliesToCondition(precondition));

        // return BxGy payload
        return new BxGyPayload("BxGy - " + (precondition.isComboBxGy ? "Combo - " : "Any - ") + LocalDateTime.now(),
                precondition.getActiveDate(),
                precondition.getExpiredDate(),
                loginInfo.getStoreID(),
                precondition.getUsageLimit(),
                conditions);
    }

    public void createBxGy() {
        APICreateBxGyV2.BxGyPrecondition precondition = new APICreateBxGyV2.BxGyPrecondition(nextBoolean(),
                nextBoolean() ? List.of() : List.of(new APISegment(loginInformation).getSegmentList().get(0).getId()),
                nextBoolean() ? List.of() : List.of(new APIProductCollection(loginInformation).getCollectionInfo().getCollectionIds().get(0)),
                List.of(new APICreateProduct(loginInformation).createWithoutVariationProduct(nextBoolean(), 5).getProductID()),
                nextBoolean() ? List.of() : List.of(loginInfo.getAssignedBranchesIds().get(0)),
                nextBoolean() ? null : nextLong(MAX_PRICE),
                nextBoolean() ? null : nextInt(100),
                nextInt(1000),
                List.of(new APICreateProduct(loginInformation).createWithoutVariationProduct(nextBoolean(), 5).getProductID()),
                nextInt(1000),
                OffsetDateTime.now().plusDays(1).toString(),
                OffsetDateTime.now().plusDays(1).toString(),
                nextBoolean() ? null : nextInt(100));

        api.post(createBxGyPath, loginInfo.getAccessToken(), getBxGyPayload(precondition))
                .prettyPrint();
    }

    public void createBxGy(BxGyPrecondition precondition) {
        api.post(createBxGyPath, loginInfo.getAccessToken(), getBxGyPayload(precondition))
                .prettyPrint();
    }
}
