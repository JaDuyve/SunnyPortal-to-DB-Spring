package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AuthenticatedHttpRequest extends SunnyPortalRequest {

    private static final String SIGNATURE_METHOD = "auth";
    private static final String SIGNATURE_VERSION = "100";

    private static final String SERVICES_PATH_SEGMENT = "/services";

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private final URI uri;

    public AuthenticatedHttpRequest(final URI baseURI, final Token tokenProperties) throws NoSuchAlgorithmException, InvalidKeyException {
        final String timestamp = tokenProperties.getTimestamp();

        this.uri = new DefaultUriBuilderFactory().builder()
                .scheme(baseURI.getScheme())
                .host(baseURI.getHost())
                .path(baseURI.getPath())
                .pathSegment(tokenProperties.getIdentifier())
                .queryParam("timestamp", timestamp)
                .queryParam("signature-method", SIGNATURE_METHOD)
                .queryParam("signature-version", SIGNATURE_VERSION)
                .queryParam("signature", generateSignature(baseURI.getPath(), timestamp, tokenProperties))
                .build();
    }

    private String generateSignature(final String oldPath, final String timestamp, final Token tokenProperties) throws NoSuchAlgorithmException, InvalidKeyException {
        final String method = this.getMethodValue().toLowerCase();
        final String service = extractServiceOfPath(oldPath);
        final String identifier = tokenProperties.getIdentifier();
        final String key = tokenProperties.getKey();

        final SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA1_ALGORITHM);
        final Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        mac.update(method.getBytes(StandardCharsets.UTF_8));
        mac.update(service.getBytes(StandardCharsets.UTF_8));
        mac.update(timestamp.getBytes(StandardCharsets.UTF_8));
        mac.update(identifier.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64String(mac.doFinal())
                .replace("\r\n", "");
    }

    private String extractServiceOfPath(final String path) {
        final int servicesPathSegmentLength = SERVICES_PATH_SEGMENT.length();
        final int lastSlashIndex = path.lastIndexOf('/');

        return path.substring(servicesPathSegmentLength + 1, lastSlashIndex);
    }

    @Override
    public URI getURI() {
        return this.uri;
    }
}
