package web.StoreFront.userprofile.myorder;

import org.openqa.selenium.By;

public class MyOrderElement {

    By loc_blkOrders = By.cssSelector(".my-order-container .group-order");
    By loc_lnkReview = By.cssSelector(".order-item__review");
	
	String loc_lnkSpecificReview = "//div[@class='group-order__header__order-id' and contains(.,': #%s')]//ancestor::div[@class='group-order']//*[@class='order-item__review']";

}
