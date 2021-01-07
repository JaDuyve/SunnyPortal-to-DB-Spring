package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jari.duyvejonck.SunnyPortaltoDB.sunnyportal.model.AuthServiceNode;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@Data
@Slf4j
@Component
public class SunnyPortalAuthenticationInterceptor implements ClientHttpRequestInterceptor {


    private AuthServiceNode tokenProperties = null;

    private final SunnyPortalConfig config;

    public SunnyPortalAuthenticationInterceptor(final SunnyPortalConfig config) {
        this.config = config;
    }

    @SneakyThrows
    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) {
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
        try {
            final HttpRequest authenticationRequest = new AuthenticatedHttpRequest(request.getURI(), this.tokenProperties);

            final ClientHttpResponse response = execution.execute(authenticationRequest, requestBody);
            if (response.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                return response;
            }

            log.warn(
                    "Authentication failed for user [{}] on url [{}:{}].",
                    this.config.getUsername(),
                    request.getMethod(),
                    request.getURI());

            this.tokenProperties = null;
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
        } catch (NoSuchAlgorithmException | InvalidKeyException | ParseException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    private boolean hasToken() {
        return this.tokenProperties != null;
    }

    private void authenticate(final ClientHttpRequestExecution execution) throws IOException {
        if (this.tokenProperties != null) {
            return;
        }

        final String username = this.config.getUsername();
        log.info("Authenticating against SunnyPortal api with user [{}].", username);

        final AuthenticateHttpRequest authRequest = new AuthenticateHttpRequest(this.config);
        try (final ClientHttpResponse authResponse = execution.execute(authRequest, new byte[0])) {
            final HttpStatus statusCode = authResponse.getStatusCode();
            if (statusCode.isError()) {
                throw HttpClientErrorException.create(
                        statusCode,
                        "User [" + username + "] failed to authenticate to SunnyPortal",
                        authResponse.getHeaders(),
                        authResponse.getBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            this.tokenProperties = extractAuthProperties(authResponse);

            log.debug("Authenticated user [{}] with SunnyPortal, received auth token [{}].", username, this.tokenProperties);
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage(), e);
        }
    }


    private AuthServiceNode extractAuthProperties(final ClientHttpResponse authResponse) throws IOException, XMLStreamException {
        final byte[] responseData = authResponse.getBody().readAllBytes();

        final XMLInputFactory f = XMLInputFactory.newFactory();
        final XMLStreamReader sr = f.createXMLStreamReader(new ByteArrayInputStream(responseData));

        XmlMapper xmlMapper = new XmlMapper();
        sr.next();
        sr.next();
        AuthServiceNode value = xmlMapper.readValue(sr, AuthServiceNode.class);
        sr.close();

        return value;
    }
}
