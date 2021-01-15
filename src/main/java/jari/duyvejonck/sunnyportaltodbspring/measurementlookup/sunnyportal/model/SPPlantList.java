package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SPPlantList {

    @JacksonXmlElementWrapper(localName = "plantlist")
    private List<SPPlant> plants;

    @NoArgsConstructor
    @JacksonXmlRootElement(localName = "plant")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SPPlant {

        @JacksonXmlProperty(isAttribute = true, localName = "oid")
        private String oid;

        @Getter
        @JacksonXmlProperty(isAttribute = true, localName = "name")
        private String name;

        public UUID getOID() {
            return UUID.fromString(this.oid);
        }
    }
}


