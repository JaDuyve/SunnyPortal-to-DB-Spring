package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SunnyPortalPlantList {

    @JacksonXmlElementWrapper(localName = "plantlist")
    private List<SunnyPortalPlant> plants;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JacksonXmlRootElement(localName = "plant")
    @NoArgsConstructor
    public static class SunnyPortalPlant {

        @JacksonXmlProperty(isAttribute = true, localName = "oid")
        private String oid;

        @JacksonXmlProperty(isAttribute = true, localName = "name")
        private String name;

    }
}


