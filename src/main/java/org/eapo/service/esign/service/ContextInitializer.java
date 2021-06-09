package org.eapo.service.esign.service;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.SocketPermission;

public class ContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext ac) {
        System.setProperty("DRPROPS", "C:\\Users\\AStal\\projects\\esign\\src\\main\\resources\\appload.properties");

        System.setProperty("java.security.policy", "C:\\Users\\AStal\\projects\\esign\\src\\main\\resources\\java.pol");

    }
}
