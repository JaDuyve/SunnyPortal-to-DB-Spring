package jari.duyvejonck.sunnyportaltodbspring;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.AuthServiceNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class MapperTest {

    @Test
    public void whenJavaGotFromXmlStr_thenCorrect() throws IOException, XMLStreamException {
        final String expectedKey = "c7fb7e58-4999-4848-a001-f40194da4719";
        final String expectedIdentifier = "afbe4602-9df6-40e1-93c9-8fdfc7cd76c8";
        final String expectedCreationDate = "1/5/2021 5:21:13 PM";

        final String example = "<?xml version=\"1.0\" encoding=\"utf-8\"?><sma.sunnyportal.services><service name=\"authentication\" method=\"get\" version=\"100\" request-oid=\"58848a3b-ce38-4967-ba67-07a793731e71\" creation-date=\"1/5/2021 5:21:13 PM\"><authentication identifier=\"afbe4602-9df6-40e1-93c9-8fdfc7cd76c8\" key=\"c7fb7e58-4999-4848-a001-f40194da4719\">OK</authentication></service></sma.sunnyportal.services>";

        final XMLInputFactory f = XMLInputFactory.newFactory();
        final XMLStreamReader sr = f.createXMLStreamReader(new ByteArrayInputStream(example.getBytes(StandardCharsets.UTF_8)));

        XmlMapper xmlMapper = new XmlMapper();
        sr.next();
        sr.next();
        AuthServiceNode value = xmlMapper.readValue(sr, AuthServiceNode.class);
        sr.close();

        assertEquals(expectedKey, value.getKey());
        assertEquals(expectedIdentifier, value.getIdentifier());
        assertEquals(expectedCreationDate, value.getCreationDate());
    }
}
