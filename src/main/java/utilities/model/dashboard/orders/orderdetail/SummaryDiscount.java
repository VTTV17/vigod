package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class SummaryDiscount {
    private double value;
    private String discountType;
    private String label;
}
