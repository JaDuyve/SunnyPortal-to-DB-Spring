package jari.duyvejonck.sunnyportaltodbspring.sunnyportal;

import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth.SunnyPortalConfig;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.model.SunnyPortalPlantList.SunnyPortalPlant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SunnyPortalLookupRestApi {

    private static final String PLANT_LIST_LOOKUP_ENDPOINT = "services/plantlist/100";

    private static final String PLANT_DATA_LOOKUP_ENDPOINT = "services/data/100";

    private final RestTemplate restTemplate;
    private final SunnyPortalConfig config;
    private final SunnyPortalDeserializer deserializer;

    public SunnyPortalLookupRestApi(final RestTemplate restTemplate,
                                    final SunnyPortalDeserializer deserializer,
                                    final SunnyPortalConfig config) {
        this.restTemplate = restTemplate;
        this.deserializer = deserializer;
        this.config = config;
    }

    @PostConstruct
    public void test() {
        final Optional<List<SunnyPortalPlant>> plants = getPlantList();
        log.info("plants: [{}]", plants);
    }

    public Optional<List<SunnyPortalPlant>> getPlantList() {
        final String lookupUrl = this.config.getBaseUrl() + PLANT_LIST_LOOKUP_ENDPOINT;
        final byte[] response = this.restTemplate.getForObject(lookupUrl, byte[].class);
        final Optional<SunnyPortalPlantList> plantlist =  deserializer.deserializeToPlantList(response);

        if (plantlist.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(plantlist.get().getPlants());
    }

    public void getPlantDataForPeriod() {
        final String lookupUrl = this.config.getBaseUrl() + PLANT_LIST_LOOKUP_ENDPOINT;
        final byte[] response = this.restTemplate.getForObject(lookupUrl, byte[].class);


    }
}
