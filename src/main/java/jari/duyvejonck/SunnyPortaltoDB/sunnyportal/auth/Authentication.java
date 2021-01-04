package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "authentication")
public class Authentication {
    @JacksonXmlProperty(isAttribute = true)
    private String key;
}
