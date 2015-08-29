package com.equivi.mailsy.service.campaign.queue;


import com.equivi.mailsy.data.entity.QueueCampaignMailerEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface QueueCampaignService {


    @Async
    void sendCampaignToQueueMailer(Long campaignId);


    /**
     *
     * @return QueueCampaignMailerEntity where MailDeliveryStatus != SUCCESS and scheduled date before or equals NOW()
     */
    List<QueueCampaignMailerEntity> getEmailQueueToSend();
}
