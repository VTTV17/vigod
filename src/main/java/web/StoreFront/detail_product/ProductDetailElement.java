package web.StoreFront.detail_product;

import org.openqa.selenium.By;

public class ProductDetailElement {
    By loc_lblProductName = By.cssSelector("[rv-text='models.productName']");
    By loc_lblSellingPrice = By.cssSelector(".price-disc");
    By loc_lblListingPrice = By.cssSelector(".price-org");
    By loc_lblVariationName = By.cssSelector("span[rv-text='variation.label']");
    String variationDropdownLocator = "[aria-owns='bs-select-%s']";
    String variationValueLocator = "//span[contains(text(), '%s ') or text() = '%s']";
    String selectedLocator = "[aria-owns='bs-select-%s']";
    By loc_lblBranchStock = By.cssSelector("#branch-list .stock");
    By loc_pnlDescription = By.cssSelector("#product-description");
    By loc_lblSoldOut = By.cssSelector(".sold-out");
    By loc_lblBranchName = By.cssSelector(".info .name");
    By loc_txtQuantity = By.cssSelector("[name = 'quantity']");
    By loc_lblFlashSale = By.cssSelector(".flash-sale");
    By loc_chkBuyInBulk = By.cssSelector(".buy-in-bulk__checkbox");
    By loc_pnlWholesalePricing = By.cssSelector(".product-wholesale-pricing");
    By loc_btnBuyNow = By.cssSelector("#button-buy-now");
    By loc_btnAddToCart = By.cssSelector("#button-add-to-cart");
    By loc_spnLoading = By.cssSelector(".loader");
    By loc_icnFilterBranch = By.cssSelector("#locationCode");
    By loc_icnSearchBranch = By.cssSelector(".input-search-branch");

    // UI check
    By loc_imgHeaderLogo = By.cssSelector(".tm-header-default-menu-layout img.gs-shop-logo");
    By loc_mnuHeaderMenu = By.cssSelector(".navbar-desktop .nav-link");
    By loc_icnHeaderSearch = By.cssSelector(".bi-search");
    By loc_icnNumberOfProductsInCart = By.cssSelector(".shoppping-cart-number");
    By loc_icnHeaderCart = By.cssSelector(".bi-cart2");
    By loc_icnUserProfile = By.cssSelector(".bi-person-circle");
    By loc_brcBreadCrumbs = By.cssSelector(".breadcrumbs a");
    By loc_lblQuantity = By.cssSelector(".quantity-box > div:nth-child(1)");
    By loc_lblAvailableBranch = By.cssSelector(".branch-list > .title");
    By loc_lblAllLocations = By.cssSelector("#locationCode > option:nth-child(1)");
    By loc_plhSearchBranchByName = By.cssSelector(".input-search-branch > input");
    By loc_lblPayment = By.cssSelector(".payment .text");
    By loc_tabDescription = By.cssSelector("#nav-description-tab");
    By loc_tabReview = By.cssSelector("#nav-review-tab");
    By loc_tblReview = By.cssSelector(".review-row");
    By loc_icnReviewStar = By.cssSelector( ".review-form .icon-star-solid");
    By loc_lblReviewTitle = By.cssSelector(".review-form #title");
    By loc_lblReviewDescription = By.cssSelector(".review-form #description");
    By loc_btnSubmitReview = By.cssSelector(".review-form .btn-submit");

    By loc_lblSimilarProducts = By.cssSelector("#similar-product .title-product-description");
    By loc_imgFooterShopLogo = By.cssSelector("img.gs-shop-logo:not(.my-avatar)");
    By loc_lblFooterCompany = By.cssSelector(".col-sm-3 > .title");
    By loc_lblFooterFollowUs = By.xpath("//div[@class = 'icon-wrapper']/parent::div/div[contains(@class, 'title')]");
    By loc_lblFooterCopyright = By.cssSelector(".col-12.text-center > span");
    By loc_seoTitle = By.cssSelector("meta[name='title']");
    By loc_seoDescription = By.cssSelector("meta[name='description']");
    By loc_seoKeyword = By.cssSelector("meta[name='keywords']");
    By loc_seoURL = By.cssSelector("meta[name='og:url']");
}
