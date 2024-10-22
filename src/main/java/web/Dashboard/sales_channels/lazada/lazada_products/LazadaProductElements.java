package web.Dashboard.sales_channels.lazada.lazada_products;

import org.openqa.selenium.By;

import java.util.List;

public class LazadaProductElements {
    By loc_ckbSelectProduct(List<Long> productId){
        return By.xpath("//td[@title='%s']/preceding-sibling::td".formatted(productId));
    }
    By loc_btnSelectAction = By.xpath("//span[text()='Select action' or text()='Chọn hành động']");
    By loc_btnCreateProductToGoSell = By.xpath("//div[text()='Create product to GoSELL' or text()='Tạo sản phẩm vào GoSELL']");
    By loc_btnUpdateProductToGoSell = By.xpath("//div[text()='Update Product to GoSELL' or text()='Cập nhật sản phẩm vào GoSELL']");
    By loc_lblFetchProductStatus = By.cssSelector(".product-fetch-status");
    By loc_icnDownloadProduct(long lazadaProductId){
        return By.xpath("//td[@title='%s']//following-sibling::td[contains(@class,'download ')]/div".formatted(lazadaProductId));
    }

}
