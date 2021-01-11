package jari.duyvejonck.sunnyportaltodbspring.config;

import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth.SunnyPortalAuthenticationInterceptor;
import jari.duyvejonck.sunnyportaltodbspring.sunnyportal.auth.SunnyPortalConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(SunnyPortalConfig.class)
public class SunnyPortalLookupConfig {


    @Bean
    public RestTemplate restTemplate(final SunnyPortalConfig config) {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add(new SunnyPortalAuthenticationInterceptor(config));
        return template;
    }
}
