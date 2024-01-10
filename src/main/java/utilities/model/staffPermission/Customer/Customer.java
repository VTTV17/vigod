package utilities.model.staffPermission.Customer;

import lombok.Data;

@Data
public class Customer {
    private CustomerManagement customerManagement = new CustomerManagement();
    private Segment segment = new Segment();
}