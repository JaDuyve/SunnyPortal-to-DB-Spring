package jari.duyvejonck.sunnyportaltodbspring.persistance;

import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview.SPPlantMeasurement;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
public class Measurement {

    private static final String TIMESTAMP_FORMAT_PATTERN = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant_oid")
    private UUID plantOID;

    @Column(name = "plant_name")
    private String plantName;

    private LocalDateTime timestamp;

    @Column(name = "min")
    private double min;

    @Column(name = "max")
    private double max;

    @Column(name = "mean")
    private double mean;

    public Measurement(final SPPlantMeasurement measurement,
                       final String plantOid,
                       final String plantName,
                       final String date) {
        this.plantOID = UUID.fromString(plantOid);
        this.plantName = plantName;
        this.min = measurement.getMin();
        this.max = measurement.getMax();
        this.mean = measurement.getMean();

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT_PATTERN);
        this.timestamp = LocalDateTime.parse(date + ' ' + measurement.getTimestamp(), formatter);
    }

}
