package com.equivi.mailsy.service.mailgun;

import com.equivi.mailsy.service.constant.dEmailerWebPropertyKey;
import com.equivi.mailsy.service.mailgun.response.MailgunResponseEventMessage;
import com.equivi.mailsy.service.mailgun.response.MailgunResponseMessage;
import com.equivi.mailsy.util.MailsyStringUtil;
import com.equivi.mailsy.web.constant.WebConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service("mailgunJerseyService")
public class MailgunJerseyEmailServiceImpl implements MailgunService {

    private static final String API_USERNAME = "api";
    private static final String DOMAIN_SANDBOX = "sandbox80dd6c12cf4c4f99bdfa256bfea7cfeb.mailgun.org";
    private static final Logger LOG = LoggerFactory.getLogger(MailgunJerseyEmailServiceImpl.class);
    @Resource
    private WebConfiguration webConfiguration;
    @Resource
    private MailgunUtility mailgunUtility;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public String sendMessage(String campaignId, String domain, String from, List<String> recipientList, List<String> ccList, List<String> bccList, String subject, String message) {

        Client client = prepareClient();
        WebResource webResource = client.resource(getWebConfigAPIUrlMessage(getDomain(domain), MailgunAPIType.MESSAGES));

        MultivaluedMapImpl form = new MultivaluedMapImpl();
        form.add(MailgunParameters.FROM.getValue(), from);
        form.add(MailgunParameters.TO.getValue(), MailsyStringUtil.buildStringWithSeparator(recipientList, ','));
        form.add(MailgunParameters.CC.getValue(), MailsyStringUtil.buildStringWithSeparator(ccList, ','));
        form.add(MailgunParameters.BCC.getValue(), MailsyStringUtil.buildStringWithSeparator(bccList, ','));
        form.add(MailgunParameters.SUBJECT.getValue(), subject);
        form.add(MailgunParameters.TRACKING.getValue(), "yes");
        form.add(MailgunParameters.TRACKING_CLICKS.getValue(), "yes");
        form.add(MailgunParameters.TRACKING_OPEN.getValue(), "yes");
        form.add(MailgunParameters.HTML.getValue(), message);

        ClientResponse clientResponse = webResource.post(ClientResponse.class, form);

        String responseBody = clientResponse.getEntity(String.class);

        MailgunResponseMessage mailgunResponseMessage = null;
        try {
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mailgunResponseMessage = objectMapper.readValue(responseBody, MailgunResponseMessage.class);
        } catch (IOException e) {
            LOG.error(e.getMessage(),e);
        }

        return mailgunResponseMessage.getId();
    }

    private Client prepareClient() {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(API_USERNAME, webConfiguration.getWebConfig(dEmailerWebPropertyKey.MAILGUN_API_KEY)));
        return client;
    }

    private String getWebConfigAPIUrlMessage(String domain, MailgunAPIType mailgunAPIType) {
        StringBuilder sbEndPointURL = new StringBuilder();

        sbEndPointURL.append(webConfiguration.getWebConfig(dEmailerWebPropertyKey.MAILGUN_WEB_URL));
        sbEndPointURL.append("/");
        sbEndPointURL.append(domain);
        sbEndPointURL.append("/");
        sbEndPointURL.append(mailgunAPIType.getValue());

        return sbEndPointURL.toString();
    }


    @Override
    public MailgunResponseEventMessage getEventForMessageId(String messageId) {
        throw new UnsupportedOperationException("getEventForMessageId not supported for this service");
    }

    @Override
    public String sendMessageWithAttachment(String campaignId, String domain, String from, List<String> recipientList, List<String> ccList, List<String> bccList, String subject, String message) {

        Client client = prepareClient();

        WebResource webResource = client.resource(getWebConfigAPIUrlMessage(getDomain(domain), MailgunAPIType.MESSAGES));
        FormDataMultiPart form = new FormDataMultiPart();
        form.field(MailgunParameters.FROM.getValue(), from);
        form.field(MailgunParameters.TO.getValue(), MailsyStringUtil.buildStringWithSeparator(recipientList, ','));
        if (ccList != null && !ccList.isEmpty()) {
            form.field(MailgunParameters.CC.getValue(), MailsyStringUtil.buildStringWithSeparator(ccList, ','));
        }
        if (bccList != null && !bccList.isEmpty()) {
            form.field(MailgunParameters.BCC.getValue(), MailsyStringUtil.buildStringWithSeparator(bccList, ','));
        }
        form.field(MailgunParameters.SUBJECT.getValue(), subject);
        form.field(MailgunParameters.TRACKING.getValue(), "yes");
        form.field(MailgunParameters.TRACKING_CLICKS.getValue(), "yes");
        form.field(MailgunParameters.TRACKING_OPEN.getValue(), "yes");

        //"<html>Inline image here: <img src=\"cid:test.jpg\"></html>");

        message = StringEscapeUtils.unescapeHtml4(message);

        String sanitizedEmailContentWithoutEmbedeedImage = mailgunUtility.sanitizeEmailContentFromEmbeddedImage(message);
        List<File> fileAttachmentList = mailgunUtility.getAttachmentFileList(message);

        form.field(MailgunParameters.HTML.getValue(), sanitizedEmailContentWithoutEmbedeedImage);


        if (fileAttachmentList != null && !fileAttachmentList.isEmpty()) {
            for (File attachmentFile : fileAttachmentList) {
                form.bodyPart(new FileDataBodyPart("inline", attachmentFile,
                        MediaType.APPLICATION_OCTET_STREAM_TYPE));

            }
        }
        ClientResponse clientResponse = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).
                post(ClientResponse.class, form);

        String responseBody = clientResponse.getEntity(String.class);

        MailgunResponseMessage mailgunResponseMessage = null;
        try {
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mailgunResponseMessage = objectMapper.readValue(responseBody, MailgunResponseMessage.class);
        } catch (IOException e) {
           LOG.error(e.getMessage(),e);
        }

        return mailgunResponseMessage.getId();
    }


    @Override
    public void deleteUnsubscribe(String domain, String emailAddress) {
        throw new UnsupportedOperationException("deleteUnsubscribe not supported for this service");
    }

    @Override
    public void registerUnsubscribe(String domain, String emailAddress) {
        throw new UnsupportedOperationException("registerUnsubscribe not supported for this service");
    }

    @Override
    public List<String> getUnsubscribeList(String domain) {
        throw new UnsupportedOperationException("getUnsubscribeList not supported for this service");
    }

    @Override
    public boolean checkIfEmailAddressHasBeenUnsubscribed(String domain, String emailAddress) {
        return false;
    }

    private String getDomain(String domain) {
        if (StringUtils.isEmpty(domain)) {
            domain = DOMAIN_SANDBOX;
        }

        return domain;
    }
}
