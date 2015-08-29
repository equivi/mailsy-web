package com.equivi.mailsy.service.worker;


import com.equivi.mailsy.data.entity.ContactEntity;
import com.equivi.mailsy.data.entity.QueueCampaignMailerEntity;
import com.equivi.mailsy.data.entity.SubscribeStatus;
import com.equivi.mailsy.service.campaign.queue.QueueCampaignService;
import com.equivi.mailsy.service.contact.ContactManagementService;
import com.equivi.mailsy.service.mailgun.MailgunService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CampaignUpdateWorker {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignUpdateWorker.class);
    @Resource
    private QueueCampaignService queueCampaignService;
    @Resource
    private CampaignActivityService campaignActivityService;
    @Resource
    private ContactManagementService contactManagementService;
    @Resource(name = "mailgunRestTemplateEmailService")
    private MailgunService mailgunService;

    //@Scheduled(fixedDelay = 30000)
    public void collectCampaignQueueAndSendMail() {

        LOG.info("Collect Campaign Queue and send email");
        List<QueueCampaignMailerEntity> emailQueueToSend = queueCampaignService.getEmailQueueToSend();

        if (CollectionUtils.isNotEmpty(emailQueueToSend)) {
            campaignActivityService.sendEmail(emailQueueToSend);
        }
    }

    @Scheduled(fixedDelay = 20000)
    public void updateUnsubscribeStatus() {

        LOG.info("Update unsubscribe status");
        List<String> unsubscribeEmailAddressList = mailgunService.getUnsubscribeList(null);

        if (unsubscribeEmailAddressList.size() > 0) {

            //Update unsubscribe status
            for (String unsubscribeEmailAddress : unsubscribeEmailAddressList) {
                ContactEntity contactEntity = contactManagementService.getContactEntityByEmailAddress(unsubscribeEmailAddress);

                if (contactEntity != null) {
                    contactEntity.setSubscribeStatus(SubscribeStatus.UNSUBSCRIBED);
                    contactManagementService.saveContactEntity(contactEntity);
                }
            }
        }

    }

    @Scheduled(fixedDelay = 5000)
    public void updateTracker() {
        LOG.info("Campaign Update tracker");
        try {
            campaignActivityService.updateTracker();
        } catch (HibernateOptimisticLockingFailureException hex) {
            LOG.error("Stale Object State exception");
        }
    }
}
