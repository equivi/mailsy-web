package com.equivi.mailsy.service.campaign;

import com.equivi.mailsy.data.dao.CampaignDao;
import com.equivi.mailsy.data.dao.SubscriberGroupDao;
import com.equivi.mailsy.data.entity.CampaignEntity;
import com.equivi.mailsy.data.entity.CampaignStatus;
import com.equivi.mailsy.dto.campaign.CampaignDTO;
import com.equivi.mailsy.service.constant.ConstantProperty;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class CampaignConverterTest {

    @Mock
    private CampaignDao campaignDao;

    @Mock
    private SubscriberGroupDao subscriberGroupDao;

    @InjectMocks
    private CampaignConverter campaignConverter;

    private SimpleDateFormat sdf;

    @Before
    public void setUp() throws Exception {
        sdf = new SimpleDateFormat(ConstantProperty.DATE_TIME_FORMAT.getValue());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvertToCampaignEntity() throws ParseException {
        //Given
        CampaignDTO campaignDTOToConvert = getCampaignDTO();

        //When
        CampaignEntity campaignEntity = campaignConverter.convertToCampaignEntity(campaignDTOToConvert);

        //Then
        assertEquals("zztop_aditya@yahoo.com", campaignEntity.getEmailFrom());
        assertEquals(CampaignStatus.DRAFT, campaignEntity.getCampaignStatus());
        assertEquals("Test content", campaignEntity.getEmailContent());
        assertEquals("C123", campaignEntity.getCampaignName());
        assertEquals(StringEscapeUtils.escapeHtml4("<ADV> Adv Weekly"), campaignEntity.getEmailSubject());
        assertNull(campaignEntity.getScheduledSendDate());
    }

    private CampaignDTO getCampaignDTO() {
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setEmailFrom("zztop_aditya@yahoo.com");
        campaignDTO.setEmailSubject("<ADV> Adv Weekly");
        campaignDTO.setScheduledSendDateTime(null);
        campaignDTO.setCampaignStatus(CampaignStatus.DRAFT.getCampaignStatusDescription());
        campaignDTO.setEmailContent("Test content");
        campaignDTO.setCampaignName("C123");

        return campaignDTO;
    }
}