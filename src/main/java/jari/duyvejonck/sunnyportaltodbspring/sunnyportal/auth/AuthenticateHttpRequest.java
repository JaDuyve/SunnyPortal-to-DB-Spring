package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class AuthenticateHttpRequest extends SunnyPortalRequest {

    private static final String AUTH_ENDPOINT = "services/authentication/100";

    private final SunnyPortalConfig config;

    private final URI uri;

    public AuthenticateHttpRequest(final SunnyPortalConfig config) {
        this.config = config;
        this.uri = new DefaultUriBuilderFactory().builder()
                .scheme(this.getScheme())
                .host(this.config.getBaseUrl())
                .path(AUTH_ENDPOINT)
                .build();
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
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

        return httpHeaders;
    }
}
