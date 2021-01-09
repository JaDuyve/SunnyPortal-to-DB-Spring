package jari.duyvejonck.SunnyPortaltoDB;

import jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
public class TokenTest {
    private final static String SIGNATURE_METHOD = "auth";
    private final static String SIGNATURE_VERSION = "100";

    private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static final String DATE_FORMAT = "M/d/yyyy h:mm:ss a";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Test
    public void generateTimestamp() {
        final String exampleDate = "1/8/2021 1:57:39 PM";
                 //                 01234567891111111111
                 //                           0123456789
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withLocale(Locale.US);
        final LocalDateTime l = LocalDateTime.parse(exampleDate, formatter);

        log.info("timestamp: [{}]", l);
    }

    @Test
    public void generateUrl() throws InvalidKeyException, NoSuchAlgorithmException {
        final String identifier = "";
        final String creationDate = "";
        final String key = "";

        final Token tokenProperties = new Token(key, identifier, creationDate);

        final URI uri = new DefaultUriBuilderFactory().builder()
                .scheme("https")
                .host("com.sunny-portal.de")
                .path("/services/plantlist")
                .pathSegment(SIGNATURE_VERSION, tokenProperties.getIdentifier())
                .queryParam("timestamp", tokenProperties.getTimestamp())
                .queryParam("signature-method", SIGNATURE_METHOD)
                .queryParam("signature-version", SIGNATURE_VERSION)
                .queryParam("signature", generateSignature("/services/plantlist", tokenProperties))
                .build();

        log.info(uri.toString());
    }

    private String generateSignature(final String oldURL, final Token tokenProperties) throws NoSuchAlgorithmException, InvalidKeyException {
        final String method = "get";
        final String service = oldURL
                .substring(oldURL.lastIndexOf('/') + 1)
                .toLowerCase(Locale.ROOT);
        final String timestamp = tokenProperties.getTimestamp();
        final String identifier = tokenProperties.getIdentifier()
                .toLowerCase(Locale.ROOT);
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

}
