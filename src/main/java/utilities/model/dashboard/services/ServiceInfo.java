package utilities.model.dashboard.services;

import lombok.Data;
import utilities.data.DataGenerator;

@Data
public class ServiceInfo {
    DataGenerator generate = new DataGenerator();

    private String serviceName = "service "+generate.generateString(10);
    private String serviceDescription = "description"+ serviceName;
    private int listingPrice = Integer.parseInt( generate.generateNumber(3)+"000");
    private int sellingPrice = listingPrice;
    private String[] locations = new String[]{"thu duc"};
    private String[] times = new String[]{"10:10"};
    private boolean isActive;
    private boolean isEnableListing = false;
    private int serviceId;
    private int serviceModelId;
}
