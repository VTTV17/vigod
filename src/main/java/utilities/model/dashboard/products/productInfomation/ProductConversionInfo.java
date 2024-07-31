package utilities.model.dashboard.products.productInfomation;

import lombok.Data;

@Data
public class ProductConversionInfo {
    private int id;
    private int conversionUnitId;
    private int quantity;
    private int itemId;
    private int modelId;
    private int itemCloneId;
    private String sku;
    private String barcode;
    private double orgPrice;
    private double newPrice;
    private double costPrice;
    private int width;
    private int weight;
    private int height;
    private int length;
    private String conversionUnitName;
}
