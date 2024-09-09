package mobile.buyer.iOS.product_detail;

import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

public class ProductDetailElement {
    By loc_lblFlashSaleBadge = xpath("//XCUIElementTypeStaticText[contains(@name,\" Sold \") or contains(@name = 'Đã bán')]");
    By loc_lblProductName = xpath("//XCUIElementTypeButton[@name=\"ic share partner\"]//following-sibling::XCUIElementTypeStaticText");
    By loc_lblSoldOutMark = xpath("//XCUIElementTypeStaticText[@name=\"Có sẵn trong 0 chi nhánh\"]|//XCUIElementTypeStaticText[@name=\"Available in 0 branches\"]");

    By loc_lblListingPrice(long listingPrice) {
        return xpath("//XCUIElementTypeStaticText[@name=\"%,d đ\"]".formatted(listingPrice));
    }

    By loc_lblDiscountCampaignBadge = xpath("//XCUIElementTypeStaticText[@name=\"Bán sỉ\"]|//XCUIElementTypeStaticText[@name=\"Wholesale\"]");
    By loc_lblWholesaleProductBadge = xpath("//XCUIElementTypeStaticText[@name=\"Giá bán sỉ\"]|//XCUIElementTypeStaticText[@name=\"Wholesale pricing\"]");

    By loc_lblVariationName(String variationName) {
        return xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(variationName));
    }

    By loc_lblVariationValue(String variationValue) {
        return xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(variationValue));
    }

    By loc_icnSearchBranch = xpath("//XCUIElementTypeButton[@name=\"ic booking search\"]");

    By loc_lblBranchAndStock(String branchName) {
        return xpath("//XCUIElementTypeStaticText[contains(@name, \"%s\")]".formatted(branchName));
    }

    By loc_lblProductDescription = xpath("//XCUIElementTypeStaticText[@name=\"Mô tả\"]/following-sibling::XCUIElementTypeWebView//XCUIElementTypeStaticText | //XCUIElementTypeStaticText[@name=\"Description\"]/following-sibling::XCUIElementTypeWebView//XCUIElementTypeStaticText");
    By loc_icnAddToCart = xpath("//XCUIElementTypeButton[@name=\"  \"]");

    By loc_lblSellingPriceOnCart(long sellingPrice) {
        return xpath("//XCUIElementTypeStaticText[@name=\"%,d đ\"]|//XCUIElementTypeStaticText[@name=\"%,d đ\"]|//XCUIElementTypeStaticText[@name=\"%,d đ\"]".formatted(sellingPrice - 1, sellingPrice, sellingPrice + 1));
    }

    By loc_chkBuyInBulk = xpath("//XCUIElementTypeStaticText[@name=\"Mua số lượng lớn\"]/preceding-sibling::XCUIElementTypeOther[1]|//XCUIElementTypeStaticText[@name=\"Buy in Bulk\"]/preceding-sibling::XCUIElementTypeOther[1]");
    By loc_txtAddToCartQuantity = xpath("//XCUIElementTypeImage[@name=\"icon_minus_small\"]//preceding-sibling::XCUIElementTypeTextField");
    By loc_icnCloseAddToCart = xpath("//XCUIElementTypeImage[@name=\"bg_variation_popup_close_button\"]");
    By loc_btnBuyNow = xpath("//XCUIElementTypeButton[@name=\"Mua ngay\"] | //XCUIElementTypeButton[@name=\"Buy now\"]");
    By loc_btnContactNow = xpath("//XCUIElementTypeButton[@name=\"Liên hệ ngay\"] | //XCUIElementTypeButton[@name=\"Contact now\"]");
}
