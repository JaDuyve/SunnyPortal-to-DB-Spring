package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@XmlRootElement(name = "service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthServiceNode {

    @JacksonXmlProperty(isAttribute = true, localName = "creation-date")
    private String creationDate;

    @JacksonXmlProperty(localName = "authentication")
    private Authentication authentication;

    public String getKey() {
        return authentication.getKey();
    }

    public String getIdentifier() {
        return authentication.getIdentifier();
    }

    public String getTimestamp() throws ParseException {

        final Date date = new SimpleDateFormat(DATE_FORMAT).parse(this.creationDate);
        return new SimpleDateFormat(TIMESTAMP_FORMAT).format(date);
    }
}
