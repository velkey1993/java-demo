package kata.concurrency.testframework.internal.model;

import okhttp3.HttpUrl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TestGivenContext {

    private final Map<String, String> queryParameterMap = new HashMap<>();
    private String path;
    private String baseUrl;

    public Map<String, String> getQueryParameterMap() {
        return queryParameterMap;
    }

    public void addQueryParameter(String key, String value) {
        this.queryParameterMap.put(key, value);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public URL buildURL() {
        HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder()
                .port(1080)
                .scheme("http")
                .host(baseUrl)
                .addPathSegments(path);
        queryParameterMap.forEach(httpUrlBuilder::addQueryParameter);
        return httpUrlBuilder.build().url();
    }
}
