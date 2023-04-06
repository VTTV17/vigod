package utilities.model.wholesaleProduct;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WholesaleProductAnalyzedData {
    private List<Long> priceList;
    private List<Integer> stockList;
    private Map<String, List<Boolean>> statusMap;
}
