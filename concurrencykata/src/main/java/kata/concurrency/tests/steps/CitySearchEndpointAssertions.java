package kata.concurrency.tests.steps;

import kata.concurrency.tests.domain.City;
import kata.concurrency.tests.domain.CitySearchEndpointResponse;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.function.Consumer;

public class CitySearchEndpointAssertions {
    public static Consumer<CitySearchEndpointResponse> returnedCitiesAre(City... expectedCities) {
        return response -> {
            List<City> actualCities = response.getCities();
            Assertions.assertThat(actualCities).contains(expectedCities);
        };
    }
}
