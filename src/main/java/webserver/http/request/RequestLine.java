package webserver.http.request;

import vo.HttpMethod;

import java.util.Map;

public class RequestLine {
    private HttpMethod method;
    private String uri;
    private String version;
    private Map<String, String> query;

    public RequestLine() {
    }

    public RequestLine(HttpMethod method, String uri, String version, Map<String, String> query) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.query = query;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method=" + method +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                ", query=" + query +
                '}';
    }
}