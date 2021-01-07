package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authentication {
    @JacksonXmlProperty(isAttribute = true, localName = "key")
    private String key;

    @JacksonXmlProperty(isAttribute = true, localName = "identifier")
    private String identifier;
}
