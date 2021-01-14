package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class SPDeserializer {

    public static <T> Optional<T> deserialize(final Class<T> type, final byte[] data) {
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        final XmlMapper mapper = new XmlMapper();

        try {
            final XMLStreamReader streamReader = factory.createXMLStreamReader(new ByteArrayInputStream(data));

            streamReader.next();
            streamReader.next();
            final T plantList = mapper.readValue(streamReader, type);
            streamReader.close();

            return Optional.of(plantList);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
