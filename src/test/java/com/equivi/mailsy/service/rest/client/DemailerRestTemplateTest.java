package com.equivi.mailsy.service.rest.client;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertTrue;

public class DemailerRestTemplateTest {

    private static final String USERNAME = "user01";
    private static final int PORT = 7000;
    private static final String PASSWORD = "password";
    private static final String URL = "user01";
    @InjectMocks
    private DemailerRestTemplate demailerRestTemplate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        demailerRestTemplate.setHostName(URL)
                .setUserName(USERNAME)
                .setPassword(PASSWORD);
    }

    @Test
    @Ignore
    public void setHttpAsyncClientFactoryTest() {

        assertTrue(demailerRestTemplate.getUserName().equals(USERNAME));
        assertTrue(demailerRestTemplate.getPassword().equals(PASSWORD));
        assertTrue(demailerRestTemplate.getHostName().equals(URL));
    }

}