package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class Token {

    private static final String DATE_FORMAT = "M/d/yyyy h:mm:ss a";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private final String key;
    private final String identifier;
    private final Duration offset;

    public Token(final String key, final String identifier, final String creationDate) {
        this.key = key;
        this.identifier = identifier;

        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(DATE_FORMAT)
                .withLocale(Locale.US);

        final LocalDateTime l = LocalDateTime.parse(creationDate, formatter);
        final LocalDateTime now = LocalDateTime.now();
        this.offset = Duration.between(l, now);
    }

    public String getTimestamp() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);
        final LocalDateTime timestamp = LocalDateTime.now().minusSeconds(offset.getSeconds());
        return timestamp.format(formatter);
    }
}
