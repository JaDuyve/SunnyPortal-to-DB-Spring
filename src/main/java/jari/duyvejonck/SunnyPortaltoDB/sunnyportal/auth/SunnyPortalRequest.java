package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

import java.net.URI;

public abstract class SunnyPortalRequest implements HttpRequest {

    private static final HttpMethod REQUEST_METHOD = HttpMethod.GET;

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
