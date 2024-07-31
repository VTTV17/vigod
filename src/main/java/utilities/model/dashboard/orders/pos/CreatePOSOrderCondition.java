package utilities.model.dashboard.orders.pos;

import api.Seller.setting.BranchManagement;
import lombok.Data;
import utilities.model.dashboard.products.productInfomation.ProductInfo;

import java.util.List;

@Data
public class CreatePOSOrderCondition {
    boolean isGuesCheckout = false;
    boolean hasDelivery  = true;
    List<ProductInfo> productInfoList;
    int branchId;
    int customerId = 0;
}
