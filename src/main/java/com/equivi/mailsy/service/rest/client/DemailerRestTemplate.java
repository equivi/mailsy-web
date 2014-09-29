package com.equivi.mailsy.service.rest.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DemailerRestTemplate extends AsyncRestTemplate {

    private int DEFAULT_TIMEOUT = 30000;

    @Resource
    private HttpAsyncClientFactory httpAsyncClientFactory;

    private String hostName;

    private String userName;

    private String password;

    private int timeout;

    public DemailerRestTemplate() {

        this.getMessageConverters().add(new StringHttpMessageConverter());

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


        MappingJacksonHttpMessageConverter mappingJacksonHttpConverter = new MappingJacksonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(new MediaType("text", "plain"));
        supportedMediaTypes.add(new MediaType("application", "json"));

        mappingJacksonHttpConverter.setSupportedMediaTypes(supportedMediaTypes);


        this.getMessageConverters().add(mappingJacksonHttpConverter);
    }

    public AsyncRestTemplate setHttpAsyncClientFactory() {
        this.setAsyncRequestFactory(httpAsyncClientFactory.getHttpComponentAsyncFactory(this.userName, this.password));
        return this;
    }

    public DemailerRestTemplate setCredentials(String userName, String password) {
        return this.setUserName(userName)
                .setPassword(password);
    }

    public int getTimeout() {
        if (this.timeout <= 0) {
            return DEFAULT_TIMEOUT;
        }
        return timeout;
    }


    public DemailerRestTemplate setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public DemailerRestTemplate setPassword(String password) {
        this.password = password;
        return this;
    }

    public DemailerRestTemplate setUserName(String userName) {
        this.userName = userName;
        return this;
    }


    public String getHostName() {
        return hostName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
