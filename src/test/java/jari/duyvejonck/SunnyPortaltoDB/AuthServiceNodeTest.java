package jari.duyvejonck.SunnyPortaltoDB;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class AuthServiceNodeTest {

    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss aaa";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Test
    public void generateTimestamp() throws ParseException {
        final String exampleDate  = "1/5/2021 5:21:13 PM";


        final Date date = new SimpleDateFormat(DATE_FORMAT).parse(exampleDate);
        log.info("date: [{}]", date);

        log.info("timestamp: [{}]", new SimpleDateFormat(TIMESTAMP_FORMAT).format(date));
    }
}
