package jari.duyvejonck.sunnyportaltodbspring.sunnyportal;

import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList.SunnyPortalPlant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SunnyPortalDeserializerTest {

    private SunnyPortalDeserializer deserializer;

    @BeforeEach
    public void setup() {
        this.deserializer = new SunnyPortalDeserializer();
    }

    @Test
    public void testDeserializePlantListResponse() {
        final Optional<String> validPlantListResponse = readTestXmlFile("sunnyportal-plant-lookup/plant-list-response.xml");
        assertTrue(validPlantListResponse.isPresent());

        final Optional<SunnyPortalPlantList> optional = deserializer.deserializeToPlantList(validPlantListResponse
                .get()
                .getBytes(StandardCharsets.UTF_8));

        assertTrue(optional.isPresent());
        final List<SunnyPortalPlant> plantList = optional.get().getPlants();
        final String expectedOid = "dd1a3875-53d8-4259-8fb2-fc47666f5f82";
        final String expectedName = "plant-name";

        assertEquals(expectedOid, plantList.get(0).getOid());
        assertEquals(expectedName, plantList.get(0).getName());
    }

    // TODO - add more test for failed state and more than 1 plant

    private static Optional<String> readTestXmlFile(final String classpathLocation) {
        final Resource resource = new ClassPathResource(classpathLocation);

        try (final Scanner s = new Scanner(new BufferedReader(new FileReader(resource.getFile())))){
            final StringBuilder sb = new StringBuilder();
            while(s.hasNextLine()) {
                sb.append(s.nextLine());
            }

            return Optional.of(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}