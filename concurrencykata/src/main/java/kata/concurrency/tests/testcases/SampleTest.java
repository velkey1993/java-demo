package kata.concurrency.tests.testcases;

import com.google.gson.Gson;
import kata.concurrency.testframework.api.TestCase;
import kata.concurrency.tests.domain.City;
import kata.concurrency.tests.domain.CitySearchEndpointResponse;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Arrays;

import static kata.concurrency.tests.domain.TestCities.*;
import static kata.concurrency.tests.steps.SampleTestSteps.*;

/**
 * This class usually a 'JBehave story file' or a 'Cucumber feature file', but that's not in scope now.
 */
public class SampleTest {

    private ClientAndServer clientAndServer;

    @PostConstruct
    public void postConstruct() {
        clientAndServer = ClientAndServer.startClientAndServer(1080);
    }

    @PreDestroy
    public void preDestroy() {
        clientAndServer.stop();
    }

    @TestCase
    public void citySearchEndpointShouldSupportKm() throws IOException {
        setUpMockServerClientForRadiusInKm();

        givenAGeoLoc(51.509865, -0.118092);
        givenARadiusInKm(5);

        whenCitySearchEndpointIsCalled();

        thenReturnedCitiesAre(LONDON);
    }

    @TestCase
    public void citySearchEndpointShouldSupportKmWrongOrdered() throws IOException {
        setUpMockServerClientForRadiusInKm();

        whenCitySearchEndpointIsCalled();

        givenAGeoLoc(51.509865, -0.118092);
        givenARadiusInKm(5);

        thenReturnedCitiesAre(LONDON);

        givenARadiusInKm(5);
    }

    @TestCase
    public void citySearchEndpointShouldSupportMiles() throws IOException {
        setUpMockServerClientForRadiusInMiles();

        givenARadiusInMile(500);
        givenAGeoLoc(48.210033, 16.363449);

        whenCitySearchEndpointIsCalled();

        thenReturnedCitiesAre(BERLIN, BUDAPEST);
    }

    @TestCase
    public void citySearchEndpointShouldSupportMilesWrongOrdered() throws IOException {
        setUpMockServerClientForRadiusInMiles();

        whenCitySearchEndpointIsCalled();

        givenARadiusInMile(500);
        givenAGeoLoc(48.210033, 16.363449);

        thenReturnedCitiesAre(BERLIN, BUDAPEST);

        givenAGeoLoc(48.210033, 16.363449);
    }

    private void setUpMockServerClientForRadiusInKm() {
        new MockServerClient("env.mydomain.com", 1080)
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("citysearch")
                        .withQueryStringParameters(
                                Parameter.param("lat", "51.509865"),
                                Parameter.param("lon", "-0.118092"),
                                Parameter.param("radius", "5"),
                                Parameter.param("radiusType", "km")
                        ))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(new Gson().toJson(createResponse(LONDON))));
    }

    private void setUpMockServerClientForRadiusInMiles() {
        new MockServerClient("env.mydomain.com", 1080)
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("citysearch")
                        .withQueryStringParameters(
                                Parameter.param("lat", "48.210033"),
                                Parameter.param("lon", "16.363449"),
                                Parameter.param("radius", "500"),
                                Parameter.param("radiusType", "mile")
                        ))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(new Gson().toJson(createResponse(BUDAPEST, BERLIN))));
    }

    private CitySearchEndpointResponse createResponse(City... cities) {
        CitySearchEndpointResponse response = new CitySearchEndpointResponse();
        Arrays.stream(cities).forEach(response::addCity);
        return response;
    }
}
