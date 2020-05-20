package kata.concurrency.tests.steps;

import kata.concurrency.testframework.api.Test;
import kata.concurrency.tests.domain.City;
import kata.concurrency.tests.domain.Deserializers;

import java.io.IOException;

/**
 * Test steps should be independent from each other. Their order shouldn't count, they shouldn't depend on each other.
 * Test steps are building blocks, any combination should be valid.
 * Except for the rule that all 'given' is before the 'when' which is before all 'then' steps.
 */
public class SampleTestSteps {

    public static void givenAGeoLoc(double lat, double lon) throws IOException {
        Test.addQueryParam("lat", String.valueOf(lat));
        Test.addQueryParam("lon", String.valueOf(lon));
    }

    public static void givenARadiusInKm(int radius) throws IOException {
        Test.addQueryParam("radius", String.valueOf(radius));
        Test.addQueryParam("radiustype", "km");
    }

    public static void givenARadiusInMile(int radius) throws IOException {
        Test.addQueryParam("radius", String.valueOf(radius));
        Test.addQueryParam("radiustype", "mile");
    }

    public static void whenCitySearchEndpointIsCalled() throws IOException {
        Test.addPath("/citysearch");
        Test.setBaseUrl("env.mydomain.com");
        Test.setResponseDeserializer(Deserializers.CITY_SEARCH_ENDPOINT);
    }

    public static void whenCitySearchEndpointIsCalledWrongOrdered() throws IOException {
        Test.setResponseDeserializer(Deserializers.CITY_SEARCH_ENDPOINT);
        Test.addPath("/citysearch");
        Test.setBaseUrl("env.mydomain.com");
    }

    public static void thenReturnedCitiesAre(City... expectedCities) throws IOException {
        Test.assertThat(CitySearchEndpointAssertions.returnedCitiesAre(expectedCities));
    }
}
