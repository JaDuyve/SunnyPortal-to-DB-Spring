package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

import java.net.URI;

public abstract class SPRequest implements HttpRequest {

    private static final HttpMethod REQUEST_METHOD = HttpMethod.GET;
    private static final String SCHEME = "https";

    protected String getScheme() {
        return SCHEME;
    }

    @Override
    public HttpMethod getMethod() {
        return REQUEST_METHOD;
    }

    @Override
    public String getMethodValue() {
        return REQUEST_METHOD.name();
    }

    @Override
    public abstract URI getURI();

    @Override
    public HttpHeaders getHeaders() {
        return new HttpHeaders();
    }
}
