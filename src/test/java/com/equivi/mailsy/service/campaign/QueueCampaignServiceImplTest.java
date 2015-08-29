package com.equivi.mailsy.service.campaign;

import com.equivi.mailsy.data.dao.QueueCampaignMailerDao;
import com.equivi.mailsy.data.entity.*;
import com.equivi.mailsy.service.campaign.queue.QueueCampaignMailerConverter;
import com.equivi.mailsy.service.campaign.queue.QueueCampaignServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class QueueCampaignServiceImplTest {

    @Mock
    private QueueCampaignMailerDao queueCampaignMailerDao;

    @Mock
    private CampaignManagementService campaignManagementService;

    @Mock
    private QueueCampaignMailerConverter queueCampaignMailerConverter;

    @InjectMocks
    private QueueCampaignServiceImpl queueCampaignServiceImpl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Ignore
    public void testSendCampaignToQueueMailer() throws Exception {
        //Given
        List<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntities = getCampaignSubscriberGroupEntityList();
        when(campaignManagementService.getEmailListToSend()).thenReturn(campaignSubscriberGroupEntities);

        List<QueueCampaignMailerEntity> queueCampaignMailerEntityList = getQueueCampaignMailEntityList();
        when(queueCampaignMailerConverter.convertToQueueCampaignMailerList(anyList())).thenReturn(queueCampaignMailerEntityList);

        //When
        //queueCampaignServiceImpl.sendCampaignToQueueMailer(campaignSubscriberGroupEntities);

        //Then
        verify(queueCampaignMailerDao).save(queueCampaignMailerEntityList);
    }

    private List<QueueCampaignMailerEntity> getQueueCampaignMailEntityList() {
        List<QueueCampaignMailerEntity> queueCampaignMailerEntityList = new ArrayList<>();

        QueueCampaignMailerEntity queueCampaignMailerEntity = new QueueCampaignMailerEntity();
        queueCampaignMailerEntity.setCampaignId(1L);
        queueCampaignMailerEntity.setRecipient("abc@email.com");
        queueCampaignMailerEntity.setQueueProcessed(QueueProcessed.PENDING.getStatus());

        QueueCampaignMailerEntity queueCampaignMailerEntity2 = new QueueCampaignMailerEntity();
        queueCampaignMailerEntity2.setCampaignId(2L);
        queueCampaignMailerEntity2.setRecipient("xsd@email.com");
        queueCampaignMailerEntity2.setQueueProcessed(QueueProcessed.PENDING.getStatus());

        queueCampaignMailerEntityList.add(queueCampaignMailerEntity);
        queueCampaignMailerEntityList.add(queueCampaignMailerEntity2);

        return queueCampaignMailerEntityList;
    }


    private List<CampaignSubscriberGroupEntity> getCampaignSubscriberGroupEntityList() {
        List<SubscriberGroupEntity> subscriberGroupEntities = buildSubscriberGroupEntityList();

        List<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntityList = new ArrayList<>();

        CampaignEntity campaignEntity = new CampaignEntity();
        campaignEntity.setCampaignName("Campaign 123");
        campaignEntity.setCampaignStatus(CampaignStatus.OUTBOX);
        campaignEntity.setCampaignUUID("123");
        campaignEntity.setId(1L);

        CampaignSubscriberGroupEntity campaignSubscriberGroupEntity = new CampaignSubscriberGroupEntity();
        campaignSubscriberGroupEntity.setCampaignEntity(campaignEntity);
        campaignSubscriberGroupEntity.setSubscriberGroupEntity(subscriberGroupEntities.get(0));


        campaignSubscriberGroupEntityList.add(campaignSubscriberGroupEntity);


        return campaignSubscriberGroupEntityList;
    }

    private List<SubscriberGroupEntity> buildSubscriberGroupEntityList() {

        //Create Contact Entity
        ContactEntity contactEntity = new ContactEntity();
        contactEntity.setFirstName("Mr.ABC");
        contactEntity.setEmailAddress("abc@email.com");

        ContactEntity contactEntity2 = new ContactEntity();
        contactEntity2.setFirstName("Mr.ABC");
        contactEntity2.setEmailAddress("abc@email.com");


        //Create Subscriber Group entity
        SubscriberGroupEntity subscriberGroupEntity = new SubscriberGroupEntity();
        subscriberGroupEntity.setGroupName("Group 123");
        subscriberGroupEntity.setStatus(GenericStatus.ACTIVE);

        SubscriberGroupEntity subscriberGroupEntity2 = new SubscriberGroupEntity();
        subscriberGroupEntity2.setGroupName("Group 456");
        subscriberGroupEntity2.setStatus(GenericStatus.ACTIVE);


        //Create SubscriberContactList for subscriber group 1
        List<SubscriberContactEntity> subscriberContactEntities = new ArrayList<>();

        SubscriberContactEntity subscriberContactEntity = new SubscriberContactEntity();
        subscriberContactEntity.setSubscriberGroupEntity(subscriberGroupEntity);
        subscriberContactEntity.setContactEntity(contactEntity);

        SubscriberContactEntity subscriberContactEntity2 = new SubscriberContactEntity();
        subscriberContactEntity2.setSubscriberGroupEntity(subscriberGroupEntity);
        subscriberContactEntity2.setContactEntity(contactEntity2);

        subscriberGroupEntity.setSubscribeContactEntityList(subscriberContactEntities);


        List<SubscriberGroupEntity> subscriberGroupEntities = new ArrayList<>();
        subscriberGroupEntities.add(subscriberGroupEntity);
        subscriberGroupEntities.add(subscriberGroupEntity2);


        return subscriberGroupEntities;
    }
}