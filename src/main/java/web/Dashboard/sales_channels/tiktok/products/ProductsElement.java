package web.Dashboard.sales_channels.tiktok.products;

import org.openqa.selenium.By;

public class ProductsElement {
    By loc_chkProduct(String tiktokProductId) {
        return By.xpath("//label[*[text() = '%s']]//input".formatted(tiktokProductId));
    }

    By loc_icnDownload(String tiktokProductId) {
        return By.xpath("//tr[td/label/*[text() = '%s']]//td[*[@data-icon='download']]".formatted(tiktokProductId));
    }

    By loc_lnkSelectActions = By.cssSelector(".gs-dropdown-action");

    /**
     * Returns a `By` locator for selecting an action based on the index.
     * <p>
     * Action indexes:
     * 0: Create product to GoSell
     * 1: Update product to GoSell
     * 2: Delete product
     *
     * @param actionIndex The index of the action (0-2).
     * @return A `By` locator for the selected action.
     */
    public By loc_lstActions(int actionIndex) {
        return By.cssSelector(".actions > div:nth-child(%d)".formatted(actionIndex + 1));
    }
    By loc_dlgConfirmation_btnOK = By.cssSelector(".modal-dialog .gs-button__green");
}
