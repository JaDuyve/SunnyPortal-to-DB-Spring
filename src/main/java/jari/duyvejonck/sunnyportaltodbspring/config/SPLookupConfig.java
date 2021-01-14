package jari.duyvejonck.sunnyportaltodbspring.config;

import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.auth.SPAuthenticationInterceptor;
import jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.auth.SPConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(SPConfig.class)
public class SPLookupConfig {


    @Bean
    public RestTemplate restTemplate(final SPConfig config) {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add(new SPAuthenticationInterceptor(config));
        return template;
    }
}
