package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SPAuthenticationInterceptorTest {

    private static final byte[] REQUEST_BODY = new byte[123];

    private SPConfig config;
    private HttpRequest request;

    @BeforeEach
    public void setup() {
        this.config = new SPConfig(
                "http://test-url/api-path/",
                "dummy-user",
                "dummy-password"
        );

        this.request = mock(HttpRequest.class);
        when(this.request.getURI()).thenReturn(URI.create(config.getBaseUrl() + "some/api/call"));
        when(this.request.getMethod()).thenReturn(HttpMethod.GET);
        when(this.request.getMethodValue()).thenReturn(HttpMethod.GET.name());
        when(this.request.getHeaders()).thenReturn(new HttpHeaders());
    }

    @Test
    public void testFailedAuthentication() throws IOException {
        final SPAuthenticationInterceptor interceptor = new SPAuthenticationInterceptor(this.config);

        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        when(execution.execute(any(), any()))
                .thenReturn(new MockClientHttpResponse(HttpStatus.UNAUTHORIZED, "dummy-response.xml"));

        assertThrows(HttpClientErrorException.Unauthorized.class, () -> interceptor.intercept(this.request, REQUEST_BODY, execution));
    }

    @Test
    public void testFailedAuthenticationAfterLogin() throws IOException {
        final SPAuthenticationInterceptor interceptor = new SPAuthenticationInterceptor(this.config);
        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        when(execution.execute(any(), any()))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "auth-success-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.UNAUTHORIZED, "dummy-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "auth-success-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.UNAUTHORIZED, "dummy-response.xml"));

        final ClientHttpResponse response = interceptor.intercept(this.request, REQUEST_BODY, execution);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testReAuthentication() throws IOException {
        final SPAuthenticationInterceptor interceptor = new SPAuthenticationInterceptor(this.config);

        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        when(execution.execute(any(), any()))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "auth-success-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "dummy-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "dummy-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.UNAUTHORIZED, "dummy-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "auth-success-response.xml"))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "dummy-response.xml"));

        for (int i = 0; i < 3; i++) {
            final ClientHttpResponse response = interceptor.intercept(request, REQUEST_BODY, execution);
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        final ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        final ArgumentCaptor<byte[]> bodyCaptor = ArgumentCaptor.forClass(byte[].class);

        verify(execution, times(6)).execute(requestCaptor.capture(), bodyCaptor.capture());
        verifyNoMoreInteractions(execution);

        final List<HttpRequest> requests = requestCaptor.getAllValues();
        final List<byte[]> bodies = bodyCaptor.getAllValues();
        int count = 0;
        assertAuthRequest(requests.get(count), bodies.get(count++));
        assertAuthenticatedRequest(requests.get(count), bodies.get(count++));
        assertAuthenticatedRequest(requests.get(count), bodies.get(count++));
        assertAuthenticatedRequest(requests.get(count), bodies.get(count++));
        assertAuthRequest(requests.get(count), bodies.get(count++));
        assertAuthenticatedRequest(requests.get(count), bodies.get(count));
    }

    @Test
    public void testMissingKey() throws IOException {
        final SPAuthenticationInterceptor interceptor = new SPAuthenticationInterceptor(this.config);

        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        when(execution.execute(any(), any()))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "auth-response-without-key.xml"));

        assertThrows(IOException.class, () -> interceptor.intercept(this.request, REQUEST_BODY, execution));
    }

    @Test
    public void testMissingIdentifier() throws IOException {
        final SPAuthenticationInterceptor interceptor = new SPAuthenticationInterceptor(this.config);

        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        when(execution.execute(any(), any()))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "auth-response-without-identifier.xml"));

        assertThrows(IOException.class, () -> interceptor.intercept(this.request, REQUEST_BODY, execution));
    }

    @Test
    public void testMissingCreationDate() throws IOException {
        final SPAuthenticationInterceptor interceptor = new SPAuthenticationInterceptor(this.config);

        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        when(execution.execute(any(), any()))
                .thenReturn(new MockClientHttpResponse(HttpStatus.OK, "auth-response-without-creation-date.xml"));

        assertThrows(IOException.class, () -> interceptor.intercept(this.request, REQUEST_BODY, execution));
    }

    private void assertAuthRequest(final HttpRequest request, final byte[] body) {
        final String credentials = this.config.getUsername() + ":" + this.config.getPassword();
        final byte[] base64CredentialData = Base64.encodeBase64(credentials.getBytes(StandardCharsets.UTF_8));
        final String expectedAuthorizationToken = "Basic " + new String(base64CredentialData);

        final HttpHeaders httpHeaders = request.getHeaders();
        final Map<String, String> headerValues = httpHeaders.toSingleValueMap();

        assertEquals(expectedAuthorizationToken, headerValues.get("Authorization"));
        assertEquals(MediaType.APPLICATION_XML.toString(), headerValues.get("Accept"));
    }

    private void assertAuthenticatedRequest(final HttpRequest request,
                                            final byte[] body) {
        assertArrayEquals(REQUEST_BODY, body);
    }
}
