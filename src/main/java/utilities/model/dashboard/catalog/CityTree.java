package utilities.model.dashboard.catalog;

import java.util.List;

import lombok.Data;

@Data
public class CityTree {
    public String code;
    public String inCountry;
    public String outCountry;
    public List<District> districts;
}
