package kata.concurrency.tests.domain;

import java.util.ArrayList;
import java.util.List;

public class CitySearchEndpointResponse {

    private final List<City> cities = new ArrayList<>();

    public List<City> getCities() {
        return cities;
    }

    public void addCity(City city) {
        cities.add(city);
    }
}
