package utilities.model.dashboard.services;

import lombok.Data;

@Data
public class ServiceInfo {
    private String serviceName;
    private String serviceDescription;
    private int listingPrice;
    private int sellingPrice;
    private String[] locations;
    private String[] times;
    private boolean isActive;
    private boolean isEnableListing;
    private int serviceId;
}
