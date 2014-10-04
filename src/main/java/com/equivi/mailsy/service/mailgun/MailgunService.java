package com.equivi.mailsy.service.mailgun;


import com.equivi.mailsy.service.mail.Attachment;
import com.equivi.mailsy.service.mailgun.response.MailgunResponseEventMessage;

import java.util.List;

public interface MailgunService {


    String sendMessage(String campaignId, String domain, String from, final List<String> recipientList, final List<String> ccList, final List<String> bccList, String subject, String message);

    MailgunResponseEventMessage getEventForMessageId(String messageId);

    void sendMailWithAttachment(final List<String> recipientList, final List<Attachment> attachmentList, final List<String> ccList, final List<String> bccList, String subject, String message);

    void deleteUnsubscribe(String domain, final String emailAddress);

    void registerUnsubscribe(String domain, final String emailAddress);
}
