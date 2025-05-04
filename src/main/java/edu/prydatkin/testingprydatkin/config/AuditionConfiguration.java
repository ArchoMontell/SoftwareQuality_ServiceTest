package edu.prydatkin.testingprydatkin.config;


/*
    @author lilbl
    @project testingPrydatkin
    @class AuditionConfiguration
    @version 1.0.0
    @since 5/4/2025 - 17.46
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@Configuration
public class AuditionConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}
