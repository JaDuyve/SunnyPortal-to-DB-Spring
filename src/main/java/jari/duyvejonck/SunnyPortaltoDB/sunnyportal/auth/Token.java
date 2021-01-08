package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import lombok.Data;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Token {

    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss aaa";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private final String key;
    private final String identifier;
    private final long offset;

    public Token(final String key, final String identifier, final String creationDate) throws ParseException {
        this.key = key;
        this.identifier = identifier;

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        final LocalDateTime l = LocalDateTime.parse(creationDate, formatter);
        final LocalDateTime now = LocalDateTime.now();
        this.offset = Duration.between(l, now).toNanos();
    }

    public String getTimestamp() {
        return null;
    }
}
