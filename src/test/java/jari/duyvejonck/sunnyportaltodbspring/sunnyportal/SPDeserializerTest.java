package jari.duyvejonck.sunnyportaltodbspring.sunnyportal;

import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.SPDeserializer;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview.SPChannel;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview.SPPlantDay;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantList;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantList.SPPlant;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SPDeserializerTest {

    // TODO - add more test for failed state and more than 1 plant

    @Test
    public void testDeserializePlantListResponse() {
        final Optional<String> validPlantListResponse = readTestXmlFile("sunnyportal-plant-lookup/plant-list-response.xml");
        assertTrue(validPlantListResponse.isPresent());

        final Optional<SPPlantList> optional = SPDeserializer.deserialize(SPPlantList.class, validPlantListResponse
                .get()
                .getBytes(StandardCharsets.UTF_8));

        assertTrue(optional.isPresent());
        final List<SPPlant> plantList = optional.get().getPlants();
        final String expectedOid = "dd1a3875-53d8-4259-8fb2-fc47666f5f82";
        final String expectedName = "plant-name";

        assertEquals(expectedOid, plantList.get(0).getOid());
        assertEquals(expectedName, plantList.get(0).getName());
    }

    @Test
    public void testDeserializeDayPlantOverview() {
        final Optional<String> validPlantDayOverviewResponse = readTestXmlFile("sunnyportal-plant-lookup/plant-day-overview-response.xml");
        assertTrue(validPlantDayOverviewResponse.isPresent());

        final Optional<SPPlantDayOverview> optional = SPDeserializer.deserialize(SPPlantDayOverview.class,
                validPlantDayOverviewResponse
                        .get()
                        .getBytes(StandardCharsets.UTF_8));


        assertTrue(optional.isPresent());
        final SPPlantDayOverview dayOverview = optional.get();
        final List<SPChannel> channels = dayOverview.getData().getChannels();
        final String expectedDayTimestamp = "11/01/2021";
        final List<String> expectedChannelNames = new ArrayList<>(Arrays.asList("Power", "Total yield"));
        final List<String> expectedChannelUnits = new ArrayList<>(Arrays.asList("kW", "kWh"));
        final double expectedAbsolute = 4807.241;
        final double expectedDifference = 1.655;
        final int expectedNumberOfMeasurements = 96;


        for (int i = 0; i < channels.size(); i++) {
            final SPChannel channel = channels.get(i);
            assertEquals(expectedChannelNames.get(i), channel.getName());
            assertEquals(expectedChannelUnits.get(i), channel.getUnit());

            final SPPlantDay overview = channel.getDay();
            assertEquals(expectedDayTimestamp, overview.getTimestamp());

            if (i == 0) {
                assertEquals(expectedNumberOfMeasurements, overview.getMeasurements().size());
            }

            if (i == 1) {
                assertEquals(expectedAbsolute, overview.getAbsolute());
                assertEquals(expectedDifference, overview.getDifference());
            }
        }
    }


    private Optional<String> readTestXmlFile(final String classpathLocation) {
        final Resource resource = new ClassPathResource(classpathLocation);

        try (final Scanner s = new Scanner(new BufferedReader(new FileReader(resource.getFile())))) {
            final StringBuilder sb = new StringBuilder();
            while (s.hasNextLine()) {
                sb.append(s.nextLine());
            }

            return Optional.of(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}