package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
@Slf4j
@Component
public class SunnyPortalAuthenticationInterceptor implements ClientHttpRequestInterceptor {


    private String authToken = null;

    private final SunnyPortalConfig config;

    public SunnyPortalAuthenticationInterceptor(final SunnyPortalConfig config) {
        this.config = config;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        final boolean hadToken = hasToken();
        if (!hadToken) {
            authenticate(execution);
        }

        return doAuthenticatedRequest(request, body, execution, hadToken);
    }

    private ClientHttpResponse doAuthenticatedRequest(final HttpRequest request,
                                                      final byte[] requestBody,
                                                      final ClientHttpRequestExecution execution,
                                                      boolean reauthenticateOnUnauthorized) throws IOException {
        final HttpRequest authenticationRequest = addAuthenticationHeaders(request);
        final ClientHttpResponse response = execution.execute(authenticationRequest, requestBody);
        if (response.getStatusCode() != HttpStatus.UNAUTHORIZED) {
            return response;
        }

        log.warn(
                "Authentication failed for user [{}] on url [{}:{}].",
                this.config.getUsername(),
                request.getMethod(),
                request.getURI());

        this.authToken = null;
        if (!reauthenticateOnUnauthorized) {
            return response;
        }

        log.warn(
                "Re-authenticating user [{}] against SunnyPortal for request [{}:{}]",
                this.config.getUsername(),
                request.getMethod(),
                request.getURI()
        );

        response.close();
        authenticate(execution);
        return doAuthenticatedRequest(request, requestBody, execution, false);

    }

    private HttpRequest addAuthenticationHeaders(final HttpRequest request) {
        final HttpHeaders requestHeaders = request.getHeaders();
        requestHeaders.set("authentication", "Bearer " + this.authToken);
        return request;
    }

    private boolean hasToken() {
        return this.authToken != null && !this.authToken.trim().isEmpty();
    }

    private void authenticate(final ClientHttpRequestExecution execution) throws IOException {
        if (this.authToken != null) {
            return;
        }

        final String username = this.config.getUsername();
        log.info("Authenticating against SunnyPortal api with user [{}].", username);

        final SunnyPortalAuthHttpRequest authRequest = new SunnyPortalAuthHttpRequest(this.config);
        try (final ClientHttpResponse authResponse = execution.execute(authRequest, new byte[0])) {
            final HttpStatus statusCode = authResponse.getStatusCode();
            if (statusCode.isError()) {
                throw HttpClientErrorException.create(
                        statusCode,
                        "User [" + username +"] failed to authenticate to SunnyPortal",
                        authResponse.getHeaders(),
                        authResponse.getBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            this.authToken = extractToken(authResponse);

            log.debug("Authenticated user [{}] with SunnyPortal, received auth token [{}].", username, this.authToken);
        }
    }


    private String extractToken(final ClientHttpResponse authResponse) throws IOException {
        final byte[] responseData = authResponse.getBody().readAllBytes();
        final String responseString = new String(responseData, StandardCharsets.UTF_8);

        final XmlMapper mapper = new XmlMapper();

        return mapper.readValue(responseString, Authentication.class).getKey();
    }
}
