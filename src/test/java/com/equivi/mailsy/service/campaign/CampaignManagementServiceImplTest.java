package com.equivi.mailsy.service.campaign;

import com.equivi.mailsy.data.dao.CampaignDao;
import com.equivi.mailsy.data.dao.CampaignSubscriberGroupDao;
import com.equivi.mailsy.data.dao.SubscriberGroupDao;
import com.equivi.mailsy.data.entity.*;
import com.equivi.mailsy.service.campaign.queue.QueueCampaignService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.rowset.Predicate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CampaignManagementServiceImplTest {

    @InjectMocks
    private CampaignManagementServiceImpl campaignManagementService;

    @Mock
    private CampaignDao campaignDao;

    @Mock
    private CampaignSubscriberGroupDao campaignSubscriberGroupDao;

    @Mock
    private SubscriberGroupDao subscriberGroupDao;

    @Mock
    private QueueCampaignService queueCampaignService;

    @Mock
    private CampaignConverter campaignConverter;

    @Mock
    private CampaignValidator campaignValidator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendCampaignToQueueMailer() throws Exception {

        //Given
        CampaignEntity campaignEntity = getCampaignEntity();

        when(campaignDao.findOne(anyLong())).thenReturn(campaignEntity);
        List<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntities = campaignEntity.getCampaignSubscriberGroupEntities();
        when(campaignSubscriberGroupDao.findAll((com.mysema.query.types.Predicate) any(Predicate.class))).thenReturn(campaignSubscriberGroupEntities);

        //When
        campaignManagementService.sendCampaignToQueueMailer(1L);

        //Then
        verify(campaignDao).save(campaignEntity);
        //verify(queueCampaignService).sendCampaignToQueueMailer(campaignSubscriberGroupEntities);
    }

    private CampaignEntity getCampaignEntity() {
        CampaignEntity campaignEntity = new CampaignEntity();
        campaignEntity.setId(1L);
        campaignEntity.setCampaignUUID("acbuudd");
        campaignEntity.setCampaignStatus(CampaignStatus.DRAFT);
        campaignEntity.setCampaignName("campaign 1");

        SubscriberGroupEntity subscriberGroupEntity = new SubscriberGroupEntity();
        subscriberGroupEntity.setGroupName("group1");
        subscriberGroupEntity.setStatus(GenericStatus.ACTIVE);

        CampaignSubscriberGroupEntity campaignSubscriberGroupEntity = new CampaignSubscriberGroupEntity();
        campaignSubscriberGroupEntity.setCampaignEntity(campaignEntity);
        campaignSubscriberGroupEntity.setSubscriberGroupEntity(subscriberGroupEntity);

        List<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntityList = new ArrayList<>();
        campaignSubscriberGroupEntityList.add(campaignSubscriberGroupEntity);

        campaignEntity.setCampaignSubscriberGroupEntities(campaignSubscriberGroupEntityList);
        return campaignEntity;
    }


}