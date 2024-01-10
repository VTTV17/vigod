package utilities.model.staffPermission.Service;

import lombok.Data;

@Data
public class Service {
    private ServiceManagement serviceManagement = new ServiceManagement();
    private ServiceCollection serviceCollection = new ServiceCollection();
}