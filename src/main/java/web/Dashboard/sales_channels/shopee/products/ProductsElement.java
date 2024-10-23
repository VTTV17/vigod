package web.Dashboard.sales_channels.shopee.products;

import org.openqa.selenium.By;

public class ProductsElement {
    By loc_txtSearchBox = By.cssSelector(".shopee-product-management .uik-input__input");
    By loc_icnDownloadProduct = By.cssSelector(".fa-download");
    By loc_spnLoading = By.cssSelector(".loading-screen");
    
    By loc_chkShopeeProductId(String shopeeProductId) {
    	return By.xpath("//a[.='%s']//preceding-sibling::label/div[contains(@class,'uik-checkbox__label')]".formatted(shopeeProductId));
    }
    
    By loc_lnkSelectAction = By.cssSelector(".gs-dropdown-action .gs-fake-link");
    By loc_ddvCreateProductToGoSELL = By.xpath("//div[@class=' actions expand']/div[starts-with(.,'Create') or starts-with(.,'Tạo')]");
    By loc_ddvUpdateProductToGoSELL = By.xpath("//div[@class=' actions expand']/div[starts-with(.,'Update') or starts-with(.,'Cập')]");
    By loc_icnSyncStatus = By.cssSelector(".synchronize-status");
}
