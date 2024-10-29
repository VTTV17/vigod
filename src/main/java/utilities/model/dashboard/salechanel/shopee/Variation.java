package utilities.model.dashboard.salechanel.shopee;

import lombok.Data;

@Data
public class Variation {
	public int id;
	public String shopeeVariationId;
	public String variationSku;
	public String name;
	public int price;
	public int stock;
	public int itemId;
	public int tierIndexId;
	public int bcModelId;
	public String tierIndexValue;
	public int syncLastStock;
}