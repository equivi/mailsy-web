package com.equivi.mailsy.service.emailverifier.webemailverifier;

import com.equivi.mailsy.dto.emailer.EmailVerifierResult;
import com.equivi.mailsy.service.constant.dEmailerWebPropertyKey;
import com.equivi.mailsy.service.emailverifier.VerifierService;
import com.equivi.mailsy.service.rest.client.DemailerRestTemplate;
import com.equivi.mailsy.util.WebConfigUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service(value = "webEmailVerifierService")
public class WebEmailVerifierServiceImpl implements VerifierService {

    private static final Logger LOG = LoggerFactory.getLogger(WebEmailVerifierServiceImpl.class);

    private static final String OPERATION = "CheckEmail";

    @Resource
    private DemailerRestTemplate restTemplate;

    @Override
    public List<EmailVerifierResult> filterValidEmail(List<String> emailList) {
        List<EmailVerifierResult> emailVerifierResponseList = new ArrayList<>();
        if (!emailList.isEmpty()) {
            for (String emailAddress : emailList) {
                emailVerifierResponseList.add(getEmailAddressStatus(emailAddress));
            }
            return emailVerifierResponseList;
        }
        return Lists.newArrayList();
    }


    @Override
    public EmailVerifierResult getEmailAddressStatus(String emailAddress) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        LOG.info("Send to Email Verifier API");
        restTemplate.setHostName(getVerifierApiUrl()).setUserName("").setPassword("");
        ResponseEntity<String> emailVerifierResponse = restTemplate.getForEntity(buildVerifyEmailAddressQueryString(emailAddress), String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LOG.info("Result:" + emailVerifierResponse.getBody());

            WebEmailVerifierResponse emailVerifierResponseEntity = objectMapper.readValue(emailVerifierResponse.getBody(), WebEmailVerifierResponse.class);
            return convertToEmailResult(emailAddress, emailVerifierResponseEntity);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private EmailVerifierResult convertToEmailResult(String emailAddress, WebEmailVerifierResponse emailVerifierResponseEntity) {
        EmailVerifierResult emailVerifierResult = new EmailVerifierResult();
        emailVerifierResult.setEmailAddress(emailAddress);
        if (emailVerifierResponseEntity.getStatusCode().equals("Success")) {
            emailVerifierResult.setStatus("Valid");
        } else {
            emailVerifierResult.setStatus("Invalid");
        }
        emailVerifierResult.setStatusDescription(emailVerifierResponseEntity.getStatus());

        return emailVerifierResult;
    }

    private String buildVerifyEmailAddressQueryString(String emailAddress) {
        StringBuilder sbQueryEmailAddress = new StringBuilder();
        sbQueryEmailAddress.append(getVerifierApiUrl());
        sbQueryEmailAddress.append("/");
        sbQueryEmailAddress.append(OPERATION);
        sbQueryEmailAddress.append("/");
        sbQueryEmailAddress.append(getApiKey());
        sbQueryEmailAddress.append("/");
        sbQueryEmailAddress.append(emailAddress);
        return sbQueryEmailAddress.toString();
    }

    private String getApiKey() {
        return WebConfigUtil.getValue(dEmailerWebPropertyKey.EMAIL_VERIFIER_API_KEY);
    }

    private String getVerifierApiUrl() {
        return WebConfigUtil.getValue(dEmailerWebPropertyKey.EMAIL_VERIFIER_URL);
    }
}
