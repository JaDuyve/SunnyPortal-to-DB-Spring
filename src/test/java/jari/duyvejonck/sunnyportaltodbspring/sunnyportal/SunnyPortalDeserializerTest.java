package jari.duyvejonck.sunnyportaltodbspring.sunnyportal;

import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantDayOverview;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantDayOverview.SunnyPortalChannel;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantDayOverview.SunnyPortalPlantDay;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList.SunnyPortalPlant;
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

class SunnyPortalDeserializerTest {

    // TODO - add more test for failed state and more than 1 plant

    @Test
    public void testDeserializePlantListResponse() {
        final Optional<String> validPlantListResponse = readTestXmlFile("sunnyportal-plant-lookup/plant-list-response.xml");
        assertTrue(validPlantListResponse.isPresent());

        final Optional<SunnyPortalPlantList> optional = SunnyPortalDeserializer.deserialize(SunnyPortalPlantList.class, validPlantListResponse
                .get()
                .getBytes(StandardCharsets.UTF_8));

        assertTrue(optional.isPresent());
        final List<SunnyPortalPlant> plantList = optional.get().getPlants();
        final String expectedOid = "dd1a3875-53d8-4259-8fb2-fc47666f5f82";
        final String expectedName = "plant-name";

        assertEquals(expectedOid, plantList.get(0).getOid());
        assertEquals(expectedName, plantList.get(0).getName());
    }

    @Test
    public void testDeserializeDayPlantOverview() {
        final Optional<String> validPlantDayOverviewResponse = readTestXmlFile("sunnyportal-plant-lookup/plant-day-overview-response.xml");
        assertTrue(validPlantDayOverviewResponse.isPresent());

        final Optional<SunnyPortalPlantDayOverview> optional = SunnyPortalDeserializer.deserialize(SunnyPortalPlantDayOverview.class,
                validPlantDayOverviewResponse
                        .get()
                        .getBytes(StandardCharsets.UTF_8));


        assertTrue(optional.isPresent());
        final SunnyPortalPlantDayOverview dayOverview = optional.get();
        final List<SunnyPortalChannel> channels = dayOverview.getData().getChannels();
        final String expectedDayTimestamp = "11/01/2021";
        final List<String> expectedChannelNames = new ArrayList<>(Arrays.asList("Power", "Total yield"));
        final List<String> expectedChannelUnits = new ArrayList<>(Arrays.asList("kW", "kWh"));
        final double expectedAbsolute = 4807.241;
        final double expectedDifference = 1.655;
        final int expectedNumberOfMeasurements = 96;


        for (int i = 0; i < channels.size(); i++) {
            final SunnyPortalChannel channel = channels.get(i);
            assertEquals(expectedChannelNames.get(i), channel.getName());
            assertEquals(expectedChannelUnits.get(i), channel.getUnit());

            final SunnyPortalPlantDay overview = channel.getDay();
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