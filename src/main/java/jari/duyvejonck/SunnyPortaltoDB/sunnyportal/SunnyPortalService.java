package jari.duyvejonck.SunnyPortaltoDB.sunnyportal;

import jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth.SunnyPortalAuthenticationInterceptor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class SunnyPortalService {

    private final RestTemplate restTemplate;
    private SunnyPortalAuthenticationInterceptor interceptor;

    @Value("sunny-portal.username")
    private String username;

    @Value("sunny-portal.password")
    private String password;

    public SunnyPortalService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void Login() {
        final HttpHeaders authenticationHeaders = getAuthenticationHeader(this.username, this.password);


    }

    private HttpHeaders getAuthenticationHeader(final String username, final String password) {
        final String credentials = username + ":" + password;
        final byte[] base64CredentialData = Base64.encodeBase64(credentials.getBytes());

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + new String(base64CredentialData));
        headers.set();
        return headers;
    }
}
