package jari.duyvejonck.sunnyportaltodbspring.sunnyportal;

import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth.SunnyPortalConfig;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantDayOverview;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList.SunnyPortalPlant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SunnyPortalLookupRestApi {

    private static final String PLANT_LIST_LOOKUP_ENDPOINT = "/services/plantlist/100";

    private static final String PLANT_DATA_LOOKUP_ENDPOINT = "/services/data/100";

    private static final String DAY_OVERVIEW = "overview-day-fifteen-total";

    private static final String CULTURE = "en-gb";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final RestTemplate restTemplate;
    private final SunnyPortalConfig config;

    public SunnyPortalLookupRestApi(final RestTemplate restTemplate,
                                    final SunnyPortalConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @PostConstruct
    public void test() {
        final Optional<List<SunnyPortalPlant>> plants = getPlantList();
        if (plants.isEmpty()) {
            return;
        }

        final Optional<SunnyPortalPlantDayOverview> dayOverview = getPlantDataForDay(plants.get().get(0).getOid(), LocalDate.now().minusDays(1));
        log.info("day overview: [{}]", dayOverview);
    }

    public Optional<List<SunnyPortalPlant>> getPlantList() {
        final String lookupUrl = "https://" + this.config.getBaseUrl() + PLANT_LIST_LOOKUP_ENDPOINT;
        final byte[] response = this.restTemplate.getForObject(lookupUrl, byte[].class);
        final Optional<SunnyPortalPlantList> plantList =  SunnyPortalDeserializer.deserialize(SunnyPortalPlantList.class, response);

        if (plantList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(plantList.get().getPlants());
    }

    public Optional<SunnyPortalPlantDayOverview> getPlantDataForDay(final String plantOID, final LocalDate day) {
        final URI uri = new DefaultUriBuilderFactory().builder()
                .host(this.config.getBaseUrl())
                .path(PLANT_DATA_LOOKUP_ENDPOINT)
                .pathSegment(plantOID, DAY_OVERVIEW, day.format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .queryParam("culture", CULTURE)
                .build();
        final byte[] response = this.restTemplate.getForObject(uri, byte[].class);

        return SunnyPortalDeserializer.deserialize(SunnyPortalPlantDayOverview.class, response);
    }
}
