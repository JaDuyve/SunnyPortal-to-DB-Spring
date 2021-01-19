package jari.duyvejonck.sunnyportaltodbspring.persistance;

import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview.SPPlantMeasurement;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeasurementTest {

    private static final String TIMESTAMP_FORMAT_PATTERN = "dd/MM/yyyy HH:mm";

    @Test
    public void testCreateMeasurement() {
        final UUID plantOID = UUID.fromString("dd1a3875-53d8-4259-8fb2-fc47666f5f82");
        final String plantName = "plant-name";
        final String date = "11/01/2021";
        final String time = "10:45";
        final double min = 0.094;
        final double max = 0.147;
        final double mean = 0.119;

        final int expectedMin = 94;
        final int expectedMax = 147;
        final int expectedMean = 119;

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT_PATTERN);
        final LocalDateTime expectedTimestamp = LocalDateTime.parse(date + ' ' + time, formatter);

        final SPPlantMeasurement spPlantMeasurement = new SPPlantMeasurement(
                time,
                min,
                max,
                mean
        );

        final Measurement measurement = new Measurement(spPlantMeasurement, plantOID, plantName, date);

        assertEquals(plantOID, measurement.getPlantOID());
        assertEquals(plantName, measurement.getPlantName());
        assertEquals(expectedMin, measurement.getMin());
        assertEquals(expectedMax, measurement.getMax());
        assertEquals(expectedMean, measurement.getMean());
        assertEquals(expectedTimestamp, measurement.getTimestamp());
    }

}