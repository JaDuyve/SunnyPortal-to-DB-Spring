package jari.duyvejonck.sunnyportaltodbspring.service;

import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.SPLookupRestApi;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview.SPPlantDay;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantList.SPPlant;
import jari.duyvejonck.sunnyportaltodbspring.persistance.Measurement;
import jari.duyvejonck.sunnyportaltodbspring.persistance.MeasurementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MeasurementUpdateJob {

    private static final String START_DATE = "sunny-portal.start-date";

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private final SPLookupRestApi measurementRestApi;
    private final MeasurementRepository measurementRepository;

    private final LocalDateTime startDate;

    public MeasurementUpdateJob(final SPLookupRestApi measurementRestApi,
                                final MeasurementRepository measurementRepository,
                                @Value("${" + START_DATE + "}") final String startDate) {
        this.measurementRestApi = measurementRestApi;
        this.measurementRepository = measurementRepository;

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        this.startDate = LocalDate.parse(startDate, formatter).atStartOfDay();
    }

    @PostConstruct
    public void test() {
        updateMeasurements();
    }

    public void updateMeasurements() {
        final Optional<List<SPPlant>> retrievedOptionalPlants = this.measurementRestApi.getPlantList();

        if (retrievedOptionalPlants.isEmpty()) {
            log.info("Unable to find Sunny-Portal power plants.");
            return;
        }

        final List<SPPlant> retrievedPlants = retrievedOptionalPlants.get();
        for (final SPPlant plant : retrievedPlants) {
            updateMeasurementsForPlant(plant);
        }
    }

    private void updateMeasurementsForPlant(final SPPlant plant) {
        final UUID plantOID = plant.getOID();
        final LocalDateTime latestMeasurementTimestamp = getPeriodStartDate(plantOID);

        List<Measurement> measurements = getMeasurementsForPeriod(plant, latestMeasurementTimestamp)
                .stream()
                .filter(measurement -> measurement.isAfter(latestMeasurementTimestamp))
                .collect(Collectors.toList());

        saveMeasurements(measurements);
    }

    private LocalDateTime getPeriodStartDate(final UUID plantOID) {
        final LocalDateTime latestMeasurementTimestamp = this.measurementRepository.getLatestMeasurementDateForPlant(plantOID);

        if (latestMeasurementTimestamp != null && latestMeasurementTimestamp.isAfter(this.startDate)) {
            return latestMeasurementTimestamp;
        } else {
            return this.startDate;
        }
    }

    private List<Measurement> getMeasurementsForPeriod(final SPPlant plant,
                                                       final LocalDateTime date) {
        final List<Measurement> measurements = new ArrayList<>();
        final Duration difference = Duration.between(date, LocalDateTime.now());

        for (int i = 0; i < difference.toDays(); i++) {
            measurements.addAll(getMeasurementsForDate(plant, date.plusDays(i)));
        }

        return measurements;
    }

    private List<Measurement> getMeasurementsForDate(final SPPlant plant,
                                                     final LocalDateTime date) {
        final Optional<SPPlantDayOverview> retrievedOptionalDayOverview = this.measurementRestApi
                .getPlantDataForDay(plant.getOID(), date);

        if (retrievedOptionalDayOverview.isEmpty()) {
            log.info(
                    "No new measurement found for period [{}-{}] at plant [{}].",
                    LocalDateTime.now(),
                    date,
                    plant.getOID());
            return new ArrayList<>();
        }

        final SPPlantDayOverview dayOverview = retrievedOptionalDayOverview.get();
        return convertDayOverviewToMeasurements(plant, dayOverview);
    }

    private List<Measurement> convertDayOverviewToMeasurements(final SPPlant plant,
                                                               final SPPlantDayOverview dayOverview) {
        final SPPlantDay powerOfDay = dayOverview.getPowerData();
        return powerOfDay
                .getMeasurements()
                .stream()
                .map(m -> new Measurement(m, plant.getOID(), plant.getName(), powerOfDay.getTimestamp()))
                .collect(Collectors.toList());
    }


    private void saveMeasurements(final List<Measurement> measurements) {
        this.measurementRepository.saveAll(measurements);

        log.info("Saved [{}] new measurements.", measurements.size());
    }
}
