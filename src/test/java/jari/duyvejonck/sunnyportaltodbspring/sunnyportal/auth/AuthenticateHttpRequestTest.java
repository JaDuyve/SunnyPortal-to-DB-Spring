package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth;

import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.auth.AuthenticateHttpRequest;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.auth.SPConfig;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticateHttpRequestTest {

    private static final String USERNAME = "dummy-user";
    private static final String PASSWORD = "dummy-pass";
    private SPConfig config;

    @BeforeEach
    public void setup() {
        this.config = new SPConfig(
                "http://test-url/some/api",
                "dummy-user",
                "dummy-pass"
        );
    }

    @Test
    public void testGetHeaders_HasAuthorizationHeader() {
        final AuthenticateHttpRequest request = new AuthenticateHttpRequest(this.config);

        final String credentials = USERNAME + ":" + PASSWORD;
        final byte[] base64CredentialData = Base64.encodeBase64(credentials.getBytes(StandardCharsets.UTF_8));
        final String expectedAuthorizationToken = "Basic " + new String(base64CredentialData);

        final HttpHeaders httpHeaders = request.getHeaders();
        final Map<String, String> headerValues = httpHeaders.toSingleValueMap();

        assertEquals(expectedAuthorizationToken, headerValues.get("Authorization"));
        assertEquals(MediaType.APPLICATION_XML.toString(), headerValues.get("Accept"));

    }

}