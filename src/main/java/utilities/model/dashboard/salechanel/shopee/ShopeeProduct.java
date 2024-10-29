package utilities.model.dashboard.salechanel.shopee;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.Data;

/**
 * This POJO is used to deserialize the response from /shopeeservices/api/items/bc-store/76492?page=0&size=50&getBcItemName=true&sort=update_time%2CDESC
 */

@Data
public class ShopeeProduct {
    public Integer id;
    public String shopeeItemId;
    public Integer branchId;
    public Integer shopeeShopId;
    public String shopeeShopName;
    public String thumbnail;
    public String shoppeeItemName;
    public String gosellStatus;
    public String bcItemName;
    public Integer bcItemId;
    public String bcItemThumbnail;
    public String currency;
    public BigDecimal price;
    public Integer stock;
    public String lastSyncDate;
    public Integer updateTime;
    public List<Variation> variations;
    public List<TierVariation> tierVariations;
    public Boolean hasVariation;
    public Boolean hasLinkErrorStatus;
    public Integer bcStoreId;
}