package edu.prydatkin.testingprydatkin.config;


/*
    @author lilbl
    @project testingPrydatkin
    @class AuditorAware
    @version 1.0.0
    @since 5/4/2025 - 17.38
*/


import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        //return Optional.of("admin");
        return Optional.of(System.getProperty("user.name"));
    }
}
