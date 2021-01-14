package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "service")
@NoArgsConstructor
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

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Authentication {
        @JacksonXmlProperty(isAttribute = true, localName = "key")
        private String key;

        @JacksonXmlProperty(isAttribute = true, localName = "identifier")
        private String identifier;
    }
}
