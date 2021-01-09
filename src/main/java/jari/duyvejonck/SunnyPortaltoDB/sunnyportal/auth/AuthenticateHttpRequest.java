package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class AuthenticateHttpRequest extends SunnyPortalRequest {

    private static final String AUTH_ENDPOINT = "authentication/100";

    private final SunnyPortalConfig config;

    private final URI uri;

    public AuthenticateHttpRequest(final SunnyPortalConfig config) {
        this.config = config;
        this.uri = URI.create(this.config.getBaseUrl() + AUTH_ENDPOINT);
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
