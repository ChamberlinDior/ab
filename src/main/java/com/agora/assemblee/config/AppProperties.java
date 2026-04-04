package com.agora.assemblee.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Storage storage = new Storage();
    private Network network = new Network();

    @Getter @Setter
    public static class Jwt {
        private String secret;
        private long expirationMinutes = 180;
        private long refreshExpirationMinutes = 1440;
    }

    @Getter @Setter
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>();
    }

    @Getter @Setter
    public static class Storage {
        private String root;
    }

    @Getter @Setter
    public static class Network {
        private String publicBaseUrl;
        private boolean discoveryEnabled = true;
    }
}
