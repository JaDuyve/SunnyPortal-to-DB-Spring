package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;


import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class SunnyPortalAuthHttpRequest implements HttpRequest {

    private static final String AUTH_ENDPOINT = "authentication/100";

    private static final HttpMethod REQUEST_METHOD = HttpMethod.GET;

    private final SunnyPortalConfig config;

    private final URI uri;

    public SunnyPortalAuthHttpRequest(final SunnyPortalConfig config) {
        this.config = config;
        this.uri = URI.create(this.config.getBaseUrl() + AUTH_ENDPOINT);
    }

    @Override
    public final HttpMethod getMethod() {
        return REQUEST_METHOD;
    }

    @Override
    public final String getMethodValue() {
        return REQUEST_METHOD.name();
    }

    @Override
    public final URI getURI() {
        return this.uri;
    }

    @Override
    public final HttpHeaders getHeaders() {
        final String credentials = config.getUsername() + ":" + config.getPassword();
        final byte[] base64CredentialData = Base64.encodeBase64(credentials.getBytes(StandardCharsets.UTF_8));

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Basic " + new String(base64CredentialData));
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        return httpHeaders;
    }
}
