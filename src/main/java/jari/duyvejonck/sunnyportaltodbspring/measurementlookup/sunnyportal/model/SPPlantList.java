package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SPPlantList {

    @JacksonXmlElementWrapper(localName = "plantlist")
    private List<SPPlant> plants;

    @Data
    @NoArgsConstructor
    @JacksonXmlRootElement(localName = "plant")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SPPlant {

        @JacksonXmlProperty(isAttribute = true, localName = "oid")
        private String oid;

        @JacksonXmlProperty(isAttribute = true, localName = "name")
        private String name;

    }
}


