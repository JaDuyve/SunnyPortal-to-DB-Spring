package jari.duyvejonck.sunnyportaltodbspring.persistance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MeasurementRepository extends CrudRepository<Measurement, Long> {

    @Query(nativeQuery = true, value =
            "SELECT m.timestamp " +
                    "FROM measurement AS m " +
                    "WHERE m.plant_oid = :plantOID " +
                    "ORDER BY m.timestamp DESC " +
                    "LIMIT 1")
    LocalDateTime getLatestMeasurementDateForPlant(final UUID plantOID);
}
