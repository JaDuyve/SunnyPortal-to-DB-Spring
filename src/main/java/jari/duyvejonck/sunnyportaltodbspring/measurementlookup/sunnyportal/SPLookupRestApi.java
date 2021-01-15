package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal;

import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.auth.SPConfig;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantDayOverview;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantList;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model.SPPlantList.SPPlant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SPLookupRestApi {

    private static final String PLANT_LIST_LOOKUP_ENDPOINT = "/services/plantlist/100";

    private static final String PLANT_DATA_LOOKUP_ENDPOINT = "/services/data/100";

    private static final String DAY_OVERVIEW = "overview-day-fifteen-total";

    private static final String CULTURE = "en-gb";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final RestTemplate restTemplate;
    private final SPConfig config;

    public SPLookupRestApi(final RestTemplate restTemplate,
                           final SPConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public Optional<List<SPPlant>> getPlantList() {
        final String lookupUrl = "https://" + this.config.getBaseUrl() + PLANT_LIST_LOOKUP_ENDPOINT;
        final byte[] response = this.restTemplate.getForObject(lookupUrl, byte[].class);
        final Optional<SPPlantList> plantList =  SPDeserializer.deserialize(SPPlantList.class, response);

        if (plantList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(plantList.get().getPlants());
    }

    public Optional<SPPlantDayOverview> getPlantDataForDay(final UUID plantOID, final LocalDateTime day) {
        final URI uri = new DefaultUriBuilderFactory().builder()
                .host(this.config.getBaseUrl())
                .path(PLANT_DATA_LOOKUP_ENDPOINT)
                .pathSegment(plantOID.toString(),
                        DAY_OVERVIEW,
                        day.format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .queryParam("culture", CULTURE)
                .build();
        final byte[] response = this.restTemplate.getForObject(uri, byte[].class);

        return SPDeserializer.deserialize(SPPlantDayOverview.class, response);
    }
}
