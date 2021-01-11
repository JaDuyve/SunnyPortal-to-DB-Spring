package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.InputStream;

public class MockClientHttpResponse implements ClientHttpResponse {

    private final HttpHeaders headers = new HttpHeaders();

    private final HttpStatus status;
    private final String responseBodyResource;

    public MockClientHttpResponse(final HttpStatus status, final String responseBodyResource) {
        this.status = status;
        this.responseBodyResource = responseBodyResource;
    }

    @Override
    public HttpStatus getStatusCode() {
        return this.status;
    }

    @Override
    public int getRawStatusCode() {
        return this.status.value();
    }

    @Override
    public String getStatusText() {
        return this.status.toString();
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getBody() {
        return getClass().getResourceAsStream(this.responseBodyResource);
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }
}
