package com.equivi.mailsy.service.emailverifier.byteplant;


import com.equivi.mailsy.dto.emailer.EmailVerifierResult;
import com.equivi.mailsy.service.constant.dEmailerWebPropertyKey;
import com.equivi.mailsy.service.emailverifier.VerifierService;
import com.equivi.mailsy.service.rest.client.DemailerRestTemplate;
import com.equivi.mailsy.util.WebConfigUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.trove.map.hash.THashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("bytePlantVerifierService")
public class BytePlantVerifierServiceImpl implements VerifierService {

    private static final Logger LOG = LoggerFactory.getLogger(BytePlantVerifierServiceImpl.class);

    private static final String API_KEY = "APIKey";
    private static final String EMAIL_ADDRESS = "EmailAddress";
    private static final String TIMEOUT = "Timeout";

    private static final String TIMEOUT_DEFAULT = "900000";

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
        return new ArrayList<>();
    }

    @Override
    public EmailVerifierResult getEmailAddressStatus(String emailAddress) {
        Map<String, String> parameters = new THashMap<>();
        parameters.put(API_KEY, getApiKey());
        parameters.put(EMAIL_ADDRESS, emailAddress);
        parameters.put(TIMEOUT, getApiTimeout());

        ResponseEntity<String> emailVerifierResponse = restTemplate.getForEntity(buildVerifyEmailAddressQueryString(emailAddress), String.class);
        LOG.info("Result:" + emailVerifierResponse);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            BytePlantEmailVerifierResponse emailVerifierResponseEntity = objectMapper.readValue(emailVerifierResponse.getBody(), BytePlantEmailVerifierResponse.class);
            return convertToEmailResult(emailAddress, emailVerifierResponseEntity);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private EmailVerifierResult convertToEmailResult(String emailAddress, BytePlantEmailVerifierResponse emailVerifierResponseEntity) {
        EmailVerifierResult emailVerifierResult = new EmailVerifierResult();
        emailVerifierResult.setEmailAddress(emailAddress);
        if(emailVerifierResponseEntity.getStatusCode().equals("200")){
            emailVerifierResult.setStatus("Valid");
        }
        else{
            emailVerifierResult.setStatus("Invalid");
        }
        emailVerifierResult.setStatusDescription(emailVerifierResponseEntity.getInfo());

        return emailVerifierResult;
    }

    private String buildVerifyEmailAddressQueryString(String emailAddress) {
        StringBuilder sbQueryEmailAddress = new StringBuilder();
        sbQueryEmailAddress.append(getVerifierApiUrl());
        sbQueryEmailAddress.append("?EmailAddress=");
        sbQueryEmailAddress.append(emailAddress);
        sbQueryEmailAddress.append("&APIKey=");
        sbQueryEmailAddress.append(getApiKey());
        sbQueryEmailAddress.append("&Timeout=");
        sbQueryEmailAddress.append(getApiTimeout());
        return sbQueryEmailAddress.toString();
    }

    private String getApiKey() {
        return WebConfigUtil.getValue(dEmailerWebPropertyKey.EMAIL_VERIFIER_API_KEY);
    }

    private String getApiTimeout() {
        String timeout = WebConfigUtil.getValue(dEmailerWebPropertyKey.EMAIL_VERIFIER_API_TIMEOUT);
        if (StringUtils.isEmpty(timeout)) {
            return TIMEOUT_DEFAULT;
        }
        return WebConfigUtil.getValue(dEmailerWebPropertyKey.EMAIL_VERIFIER_API_TIMEOUT);
    }

    private String getVerifierApiUrl() {
        return WebConfigUtil.getValue(dEmailerWebPropertyKey.EMAIL_VERIFIER_URL);
    }
}
