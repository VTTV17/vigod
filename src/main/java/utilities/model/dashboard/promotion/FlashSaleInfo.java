package utilities.model.dashboard.promotion;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FlashSaleInfo {
    private List<Long> flashSalePrice;
    private List<Integer> flashSaleStock;
    private Map<String, List<String>> flashSaleStatus;
}
