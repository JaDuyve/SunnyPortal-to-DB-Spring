package jari.duyvejonck.sunnyportaltodbspring.persistance;

import jari.duyvejonck.sunnyportaltodbspring.Utils.ConvertUtils;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview.SPPlantMeasurement;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Table(name = "measurement")
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
    private int min;

    @Column(name = "max")
    private int max;

    @Column(name = "mean")
    private int mean;

    public Measurement(final SPPlantMeasurement measurement,
                       final UUID plantOID,
                       final String plantName,
                       final String date) {
        this.plantOID = plantOID;
        this.plantName = plantName;
        this.min = ConvertUtils.kwToW(measurement.getMin());
        this.max = ConvertUtils.kwToW(measurement.getMax());
        this.mean = ConvertUtils.kwToW(measurement.getMean());

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT_PATTERN);
        this.timestamp = LocalDateTime.parse(date + ' ' + measurement.getTimestamp(), formatter);
    }

    public boolean isAfter(final LocalDateTime timestamp) {
        return this.timestamp.isAfter(timestamp);
    }

}
