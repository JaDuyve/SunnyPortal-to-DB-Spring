package jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Plant {

    @JacksonXmlProperty(isAttribute = true, localName = "oid")
    private String oid;

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

}
