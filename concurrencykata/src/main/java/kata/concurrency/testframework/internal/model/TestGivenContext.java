package kata.concurrency.testframework.internal.model;

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
}
