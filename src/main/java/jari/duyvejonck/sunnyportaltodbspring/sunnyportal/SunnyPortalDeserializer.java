package jari.duyvejonck.sunnyportaltodbspring.sunnyportal;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Component
public class SunnyPortalDeserializer {

    private final XMLInputFactory factory;
    private final XmlMapper mapper;

    public SunnyPortalDeserializer() {
        this.factory = XMLInputFactory.newFactory();
        this.mapper = new XmlMapper();
    }

    public Optional<SunnyPortalPlantList> deserializeToPlantList(final byte[] data) {
        try {
            final XMLStreamReader streamReader = factory.createXMLStreamReader(new ByteArrayInputStream(data));

            streamReader.next();
            streamReader.next();
            final SunnyPortalPlantList plantList = this.mapper.readValue(streamReader, SunnyPortalPlantList.class);
            streamReader.close();

            return Optional.of(plantList);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
