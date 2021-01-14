package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.auth;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.AuthServiceNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
@Slf4j
@Component
public class SPAuthenticationInterceptor implements ClientHttpRequestInterceptor {

    private final SPConfig config;

    private Token tokenProperties = null;

    public SPAuthenticationInterceptor(final SPConfig config) {
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
        final HttpRequest authenticationRequest;

        try {
            authenticationRequest = new AuthenticatedHttpRequest(request.getURI(), this.tokenProperties);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IOException(e.getMessage(), e);
        }

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
                        "User [" + username + "] failed to authenticate to SunnyPortal.",
                        authResponse.getHeaders(),
                        authResponse.getBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            this.tokenProperties = extractAuthProperties(authResponse);

            log.info("Authenticated user [{}] with SunnyPortal, received auth token [{}].", username, this.tokenProperties);
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage(), e);
        }
    }


    private Token extractAuthProperties(final ClientHttpResponse authResponse) throws IOException, XMLStreamException {
        final byte[] responseData = authResponse.getBody().readAllBytes();

        final XMLInputFactory factory = XMLInputFactory.newFactory();
        final XMLStreamReader streamReader = factory.createXMLStreamReader(new ByteArrayInputStream(responseData));

        XmlMapper xmlMapper = new XmlMapper();
        streamReader.next();
        streamReader.next();
        AuthServiceNode responseValue = xmlMapper.readValue(streamReader, AuthServiceNode.class);
        streamReader.close();

        if (responseValue.getKey() == null || responseValue.getIdentifier() == null || responseValue.getCreationDate() == null) {
            throw new IOException("Not all token properties are present.");
        }

        return new Token(responseValue.getKey(), responseValue.getIdentifier(), responseValue.getCreationDate());
    }
}
