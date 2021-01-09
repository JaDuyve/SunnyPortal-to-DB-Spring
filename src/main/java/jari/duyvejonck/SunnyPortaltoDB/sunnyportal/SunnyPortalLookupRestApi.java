package jari.duyvejonck.SunnyPortaltoDB.sunnyportal;

import jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth.SunnyPortalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
public class SunnyPortalLookupRestApi {

    private final static String PLANT_LIST_LOOKUP_ENDPOINT = "plantlist";

    private final RestTemplate restTemplate;
    private final SunnyPortalConfig config;

    public SunnyPortalLookupRestApi(final RestTemplate restTemplate,
                                    final SunnyPortalConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @PostConstruct
    public void test() {
        getPlantList();
    }

    public List<String> getPlantList() {
        final String lookupUrl = this.config.getBaseUrl() + PLANT_LIST_LOOKUP_ENDPOINT;
        final String result = this.restTemplate.getForObject(lookupUrl, String.class);
        log.info(result);
        return null;
    }
}
