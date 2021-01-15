package jari.duyvejonck.sunnyportaltodbspring.measurementlookup.sunnyportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SPPlantDayOverview {

    @JacksonXmlProperty(localName = "data")
    private SPData data;

    public SPPlantDay getPowerData() {
        return this.data
                .getChannels()
                .get(0)
                .getDay();
    }

    public SPPlantDay getTotalYieldDay() {
        return this.data
                .getChannels()
                .get(1)
                .getDay();
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SPData {

        @JacksonXmlElementWrapper(localName = "overview-day-fifteen-total")
        private List<SPChannel> channels;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SPChannel {

        @JacksonXmlProperty(localName = "day")
        private SPPlantDay day;

        @JacksonXmlProperty(isAttribute = true, localName = "name")
        private String name;

        @JacksonXmlProperty(isAttribute = true, localName = "unit")
        private String unit;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SPPlantDay {

        @JacksonXmlProperty(isAttribute = true, localName = "timestamp")
        private String timestamp;

        @JacksonXmlProperty(isAttribute = true, localName = "absolute")
        private double absolute;

        @JacksonXmlProperty(isAttribute = true, localName = "difference")
        private double difference;

        @JacksonXmlProperty(localName = "fiveteen")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<SPPlantMeasurement> measurements;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SPPlantMeasurement {

        @JacksonXmlProperty(isAttribute = true, localName = "timestamp")
        private String timestamp;

        @JacksonXmlProperty(isAttribute = true, localName = "min")
        private double min;

        @JacksonXmlProperty(isAttribute = true, localName = "max")
        private double max;

        @JacksonXmlProperty(isAttribute = true, localName = "mean")
        private double mean;
    }
}
