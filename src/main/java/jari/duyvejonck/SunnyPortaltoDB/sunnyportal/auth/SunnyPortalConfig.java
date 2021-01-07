package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "sunny-portal")
public class SunnyPortalConfig {

    private String version;

    private String baseUrl;

    private String username;

    private String password;
}
