import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;

public class BH_9753 extends BaseTest{
    String sfDomain;
    CreateProduct createProduct;
    String sellerAccount = "stgauto@nbobd.com";
    String sellerPassword = "Abc@12345";

    @Test
    void case1() {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        createProduct = new CreateProduct();

        createProduct.getTaxList().getActiveBranchList();

        boolean isIMEIProduct = false;
        int branchStock = 0;
        createProduct.createWithoutVariationProduct(isIMEIProduct, 1,
                1, 1, branchStock);

        System.out.println(new ProductDetailPage(driver)
                .branchListIsShownOnSF());
    }
}
