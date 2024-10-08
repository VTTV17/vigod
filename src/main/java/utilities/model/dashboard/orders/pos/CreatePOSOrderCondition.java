package utilities.model.dashboard.orders.pos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import utilities.enums.PaymentMethod;
import utilities.enums.PaymentStatus;
import utilities.enums.pos.ReceivedAmountType;
import utilities.model.dashboard.orders.orderdetail.EarningPoint;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import web.Dashboard.orders.pos.create_order.POSPage;

import java.util.List;

import static utilities.enums.pos.ReceivedAmountType.FULL;
import static web.Dashboard.orders.pos.create_order.POSPage.POSPaymentMethod.CASH;
import static web.Dashboard.orders.pos.create_order.POSPage.UsePointType.NONE;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePOSOrderCondition {
    boolean isGuesCheckout = false;
    boolean hasDelivery  = true;
    List<ProductInfo> productInfoList;
    int branchId = 0;
    int customerId = 0;
    boolean isStaffCreateOrder;
    POSPage.UsePointType usePointType = NONE;
    ReceivedAmountType receivedAmountType = FULL;
    boolean hasEarnPoint;
    boolean isApplyPromotion;
    POSPage.POSPaymentMethod paymentMethod = CASH;
}
