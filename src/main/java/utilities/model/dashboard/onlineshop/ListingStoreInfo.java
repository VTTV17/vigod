package utilities.model.dashboard.onlineshop;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ListingStoreInfo {
   private boolean enabledEmailProduct;
   private boolean enabledEmailService;
   private boolean enabledPhoneProduct;
   private boolean enabledPhoneService;
   private boolean enabledProduct;
   private boolean enabledService;
   private boolean enabledZaloProduct;
   private boolean enabledZaloService;
   private String phoneService = "0703618433";
   private String phoneProduct = "0703618433";
}
