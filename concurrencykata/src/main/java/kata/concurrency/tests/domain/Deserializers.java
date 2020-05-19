package kata.concurrency.tests.domain;

import com.google.gson.Gson;

import java.util.function.Function;

public class Deserializers {
    private static final Gson GSON = new Gson();

    public static final Function<String, ?> CITY_SEARCH_ENDPOINT = x -> GSON.fromJson(x, CitySearchEndpointResponse.class);
}
