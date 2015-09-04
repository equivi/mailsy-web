package com.equivi.mailsy.service.campaign;


import com.equivi.mailsy.data.dao.CampaignDao;
import com.equivi.mailsy.data.entity.CampaignEntity;
import com.equivi.mailsy.data.entity.CampaignStatus;
import com.equivi.mailsy.dto.campaign.CampaignDTO;
import com.equivi.mailsy.service.constant.ConstantProperty;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class CampaignConverter {

    static final Logger LOG = LoggerFactory.getLogger(CampaignConverter.class);
    @Resource
    private CampaignDao campaignDao;

    public CampaignEntity convertToCampaignEntity(CampaignDTO campaignDTO) {

        SimpleDateFormat sdf = new SimpleDateFormat(ConstantProperty.DATE_TIME_FORMAT.getValue());

        CampaignEntity campaignEntity;
        if (campaignDTO.getId() != null) {
            campaignEntity = campaignDao.findOne(campaignDTO.getId());
        } else {
            campaignEntity = new CampaignEntity();
            campaignEntity.setCampaignUUID(UUID.randomUUID().toString());
        }

        if (StringUtils.isNotEmpty(campaignDTO.getEmailContent())) {
            String emailContent = StringEscapeUtils.unescapeHtml4(campaignDTO.getEmailContent());
            campaignEntity.setEmailContent(StringEscapeUtils.escapeHtml4(emailContent));
        }

        campaignEntity.setEmailSubject(StringEscapeUtils.escapeHtml4(campaignDTO.getEmailSubject()));
        campaignEntity.setCampaignName(campaignDTO.getCampaignName());
        campaignEntity.setEmailFrom(campaignDTO.getEmailFrom());
        campaignEntity.setCampaignStatus(CampaignStatus.getStatusByDescription(campaignDTO.getCampaignStatus()));


//        if (!StringUtils.isEmpty(campaignDTO.getScheduledSendDateTime())) {
//            try {
//                campaignEntity.setScheduledSendDate(sdf.parse(campaignDTO.getScheduledSendDateTime()));
//            } catch (ParseException e) {
//                LOG.error(e.getMessage(), e);
//            }
//        }
        return campaignEntity;
    }

    public CampaignDTO convertToCampaignDTO(CampaignEntity campaignEntity) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantProperty.DATE_FORMAT.getValue());
        SimpleDateFormat timeFormat = new SimpleDateFormat(ConstantProperty.TIME_FORMAT.getValue());
        SimpleDateFormat sdf = new SimpleDateFormat(ConstantProperty.DATE_TIME_FORMAT.getValue());

        CampaignDTO campaignDTO = new CampaignDTO();

        campaignDTO.setId(campaignEntity.getId());
        campaignDTO.setCampaignName(campaignEntity.getCampaignName());
        campaignDTO.setEmailFrom(campaignEntity.getEmailFrom());
        campaignDTO.setCampaignStatus(campaignEntity.getCampaignStatus().getCampaignStatusDescription());

        if (StringUtils.isNotEmpty(campaignEntity.getEmailContent())) {
            campaignDTO.setEmailContent(campaignEntity.getEmailContent());
        }
        campaignDTO.setEmailSubject(StringEscapeUtils.unescapeHtml4(campaignEntity.getEmailSubject()));

        Date scheduledSendDate = campaignEntity.getScheduledSendDate();
        if (scheduledSendDate == null) {
            scheduledSendDate = new Date();
        }
        campaignDTO.setScheduledSendTime(timeFormat.format(scheduledSendDate));
        campaignDTO.setScheduledSendDateTime(sdf.format(scheduledSendDate));
        campaignDTO.setScheduledSendDate(dateFormat.format(scheduledSendDate));


        return campaignDTO;
    }
}
