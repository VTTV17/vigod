package utilities.model.dashboard.products.wholesaleProduct;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WholesaleProductInfo {
    private List<Long> priceList;
    private List<Integer> stockList;
    private Map<String, List<Boolean>> statusMap;
}
