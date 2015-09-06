package com.equivi.mailsy.listener;

import com.equivi.mailsy.listener.exception.InvalidMailsyAppException;
import com.equivi.mailsy.service.constant.dEmailerWebPropertyKey;
import com.equivi.mailsy.service.rest.client.DemailerRestTemplate;
import com.equivi.mailsy.web.constant.WebConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryCheckListener implements ApplicationListener<ContextRefreshedEvent> {

    private Logger LOG = Logger.getLogger(RegistryCheckListener.class);

    @Autowired
    private DemailerRestTemplate demailerRestTemplate;

    @Autowired
    private WebConfiguration webConfiguration;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        LOG.info("Checking Registry");
        ResponseEntity<String> result = demailerRestTemplate.getForEntity(getRegistrationCheckUrl().replace("${registeredKey}", getRegisteredKey()), String.class);

        if (!Boolean.valueOf(result.getBody())) {
            throw new InvalidMailsyAppException("Application unable to start up,invalid registered key or application already expired");
        }
    }

    String getRegistrationCheckUrl() {
        return webConfiguration.getWebConfig(dEmailerWebPropertyKey.REGISTRY_CHECK_WEB_URL);
    }

    String getRegisteredKey() {
        return webConfiguration.getWebConfig(dEmailerWebPropertyKey.REGISTRATION_KEY);
    }
}
