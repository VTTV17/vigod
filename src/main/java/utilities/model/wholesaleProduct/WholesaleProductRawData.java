package utilities.model.wholesaleProduct;

import lombok.Data;

import java.util.List;

@Data
public class WholesaleProductRawData {
    private String barcode;
    private List<Integer> stock;
    private List<Long> price;
}

