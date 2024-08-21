package utilities.model.dashboard.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerAddressFull {
    private String country;
    private String city;
    private String district;
    private String ward;
}
