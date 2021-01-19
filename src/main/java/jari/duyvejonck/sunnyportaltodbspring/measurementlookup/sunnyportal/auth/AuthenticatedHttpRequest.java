package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.auth;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AuthenticatedHttpRequest extends SPRequest {

    private static final String SIGNATURE_METHOD = "auth";
    private static final String SIGNATURE_VERSION = "100";

    private static final String SERVICES_PATH_SEGMENT = "/services";

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private final URI uri;

    public AuthenticatedHttpRequest(final URI baseURI, final Token tokenProperties) throws NoSuchAlgorithmException, InvalidKeyException {
        this.uri = buildURI(baseURI, tokenProperties);
    }

    private URI buildURI(final URI baseURI, final Token tokenProperties) throws InvalidKeyException, NoSuchAlgorithmException {
        final String timestamp = tokenProperties.getTimestamp();
        final String signature = generateSignature(baseURI.getPath(), timestamp, tokenProperties);

        UriBuilder builder = new DefaultUriBuilderFactory().builder()
                .scheme(this.getScheme())
                .host(baseURI.getHost())
                .path(baseURI.getPath());

        if (baseURI.getRawQuery() == null) {
            builder = builder.pathSegment(tokenProperties.getIdentifier());
        } else {
            builder = builder
                    .query(baseURI.getQuery())
                    .queryParam("identifier", tokenProperties.getIdentifier());
        }

        return builder
                .queryParam("timestamp", timestamp)
                .queryParam("signature-method", SIGNATURE_METHOD)
                .queryParam("signature-version", SIGNATURE_VERSION)
                .queryParam("signature", signature.substring(0, signature.length() - 2))
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

        return Base64.encodeBase64String(mac.doFinal());
    }

    private String extractServiceOfPath(final String path) {
        final int servicesPathSegmentLength = SERVICES_PATH_SEGMENT.length();
        final int lastSlashIndex = path.indexOf("/" + SIGNATURE_VERSION);

        return path.substring(servicesPathSegmentLength + 1, lastSlashIndex);
    }

    @Override
    public URI getURI() {
        return this.uri;
    }
}
