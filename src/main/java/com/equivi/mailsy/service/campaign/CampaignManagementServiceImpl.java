package com.equivi.mailsy.service.campaign;

import com.equivi.mailsy.data.dao.CampaignDao;
import com.equivi.mailsy.data.dao.CampaignSubscriberGroupDao;
import com.equivi.mailsy.data.dao.SubscriberGroupDao;
import com.equivi.mailsy.data.entity.CampaignEntity;
import com.equivi.mailsy.data.entity.CampaignStatus;
import com.equivi.mailsy.data.entity.CampaignSubscriberGroupEntity;
import com.equivi.mailsy.data.entity.QCampaignEntity;
import com.equivi.mailsy.data.entity.QCampaignSubscriberGroupEntity;
import com.equivi.mailsy.data.entity.SubscriberGroupEntity;
import com.equivi.mailsy.dto.campaign.CampaignDTO;
import com.equivi.mailsy.service.campaign.queue.QueueCampaignService;
import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CampaignManagementServiceImpl implements CampaignManagementService {

    @Resource
    private CampaignDao campaignDao;

    @Resource
    private CampaignSubscriberGroupDao campaignSubscriberGroupDao;

    @Resource
    private QueueCampaignService queueCampaignService;

    @Resource
    private SubscriberGroupDao subscriberGroupDao;

    @Resource
    private CampaignConverter campaignConverter;

    @Resource
    private CampaignValidator campaignValidator;

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignEntity> getCampaignEntityPage(Map<CampaignSearchFilter, String> searchFilter, int pageNumber, int maxRecords) {

        Pageable pageable = getPageable(pageNumber - 1, maxRecords);
        Predicate campaignQueryPredicate = getCampaignPredicate(searchFilter);

        Page<CampaignEntity> requestedPage = campaignDao.findAll(campaignQueryPredicate, pageable);

        return requestedPage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignSubscriberGroupEntity> getEmailListToSend() {

        List<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntities = null;

        Iterable<CampaignEntity> campaignsWithOutboxStatus = campaignDao.findAll(getCampaignToSendPredicate());

        if (campaignsWithOutboxStatus.iterator().hasNext()) {
            campaignSubscriberGroupEntities = Lists.newArrayList(campaignSubscriberGroupDao.findAll(getCampaignSubscriberGroupPredicate(campaignsWithOutboxStatus)));
        }
        return campaignSubscriberGroupEntities;
    }


    private Pageable getPageable(int page, int maxRecords) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(page, maxRecords, sort);
    }


    private Predicate getCampaignPredicate(Map<CampaignSearchFilter, String> filterMap) {

        QCampaignEntity qCampaignEntity = QCampaignEntity.campaignEntity;
        BooleanBuilder booleanMerchantPredicateBuilder = new BooleanBuilder();

        if (filterMap.get(CampaignSearchFilter.CAMPAIGN_SUBJECT) != null) {
            booleanMerchantPredicateBuilder.or(qCampaignEntity.emailSubject.like("%" + filterMap.get(CampaignSearchFilter.CAMPAIGN_SUBJECT) + "%"));
        }

        if (filterMap.get(CampaignSearchFilter.CAMPAIGN_STATUS) != null) {
            booleanMerchantPredicateBuilder.or(qCampaignEntity.campaignStatus.eq(CampaignStatus.getStatusByDescription(filterMap.get(CampaignSearchFilter.CAMPAIGN_STATUS))));
        }

        if (filterMap.get(CampaignSearchFilter.CAMPAIGN_NAME) != null) {
            booleanMerchantPredicateBuilder.or(qCampaignEntity.campaignName.like("%" + filterMap.get(CampaignSearchFilter.CAMPAIGN_NAME) + "%"));
        }

        return booleanMerchantPredicateBuilder;
    }

    private Predicate getCampaignToSendPredicate() {

        QCampaignEntity qCampaignEntity = QCampaignEntity.campaignEntity;
        BooleanBuilder booleanMerchantPredicateBuilder = new BooleanBuilder();

        booleanMerchantPredicateBuilder.or(qCampaignEntity.campaignStatus.eq(CampaignStatus.OUTBOX));

        return booleanMerchantPredicateBuilder;
    }

    private Predicate getCampaignSubscriberGroupPredicate(CampaignEntity campaignEntity) {

        QCampaignSubscriberGroupEntity qCampaignSubscriberGroupEntity = QCampaignSubscriberGroupEntity.campaignSubscriberGroupEntity;
        BooleanBuilder booleanMerchantPredicateBuilder = new BooleanBuilder();

        booleanMerchantPredicateBuilder.or(qCampaignSubscriberGroupEntity.campaignEntity.eq(campaignEntity));

        return booleanMerchantPredicateBuilder;
    }

    private Predicate getCampaignSubscriberGroupPredicate(Iterable<CampaignEntity> campaignEntityListWithOutboxStatus) {

        QCampaignSubscriberGroupEntity qCampaignSubscriberGroupEntity = QCampaignSubscriberGroupEntity.campaignSubscriberGroupEntity;

        BooleanBuilder booleanCampaignSubscriberGroupBuilder = new BooleanBuilder();

        for (CampaignEntity campaignEntity : campaignEntityListWithOutboxStatus) {
            booleanCampaignSubscriberGroupBuilder.or(qCampaignSubscriberGroupEntity.campaignEntity.eq(campaignEntity));
        }

        return booleanCampaignSubscriberGroupBuilder;
    }


    @Override
    @Transactional(readOnly = false)
    public CampaignDTO saveCampaignDTO(CampaignDTO campaignDTO) {

        CampaignEntity campaignEntity = saveCampaignEntity(campaignDTO);

        CampaignDTO updatedCampaignDTO = campaignConverter.convertToCampaignDTO(campaignEntity);

        if (campaignEntity.getId() != null) {
            updateCampaignSubscriberGroup(campaignDTO, campaignEntity);

            updatedCampaignDTO = assignSubscriberGroupIdToDTO(updatedCampaignDTO, campaignDTO);
        }


        return updatedCampaignDTO;
    }

    @Override
    @Transactional(readOnly = false)
    public void sendCampaignToQueueMailer(Long campaignId) {
        CampaignEntity campaignEntity = campaignDao.findOne(campaignId);
        campaignEntity.setCampaignStatus(CampaignStatus.SEND);
        campaignDao.save(campaignEntity);

        //Send to queue mailer
        //List<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntityList = Lists.newArrayList(campaignSubscriberGroupDao.findAll(getCampaignSubscriberGroupPredicate(campaignEntity)));

        queueCampaignService.sendCampaignToQueueMailer(campaignId);
    }

    private CampaignEntity saveCampaignEntity(CampaignDTO campaignDTO) {
        campaignValidator.validate(campaignDTO);

        CampaignEntity campaignEntity = campaignConverter.convertToCampaignEntity(campaignDTO);

        campaignEntity = campaignDao.save(campaignEntity);

        updateCampaignSubscriberGroup(campaignDTO, campaignEntity);
        return campaignEntity;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveCampaign(CampaignDTO campaignDTO) {
        saveCampaignEntity(campaignDTO);
    }

    private CampaignDTO assignSubscriberGroupIdToDTO(CampaignDTO updatedCampaignDTO, CampaignDTO campaignDTO) {
        if (campaignDTO.getId() != null) {
            Iterable<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntities = campaignSubscriberGroupDao.findAll(buildCampaignSubscriberGroupPredicate(campaignDTO.getId()));
            List<Long> subscriberGroupIds = new ArrayList<>();
            for (CampaignSubscriberGroupEntity campaignSubscriberGroupEntity : campaignSubscriberGroupEntities) {

                Long subscriberGroupId = campaignSubscriberGroupEntity.getSubscriberGroupEntity().getId();
                subscriberGroupIds.add(subscriberGroupId);

            }
            updatedCampaignDTO.setSubscriberGroupIds(subscriberGroupIds);
        }
        return updatedCampaignDTO;
    }

    private void updateCampaignSubscriberGroup(CampaignDTO campaignDTO, CampaignEntity campaignEntity) {
        if (campaignDTO.getSubscriberGroupIds() != null && !campaignDTO.getSubscriberGroupIds().isEmpty()) {
            //Delete existing campaign subscriber group for this Campaign Entity
            deleteCampaignSubscriberGroup(campaignDTO);

            //Save new relation campaign subscriber group
            saveCampaignSubcriberGroup(campaignDTO, campaignEntity);
        }
    }

    private void deleteCampaignSubscriberGroup(CampaignDTO campaignDTO) {
        Predicate getCampaignSubscriberGroupPredicate = buildCampaignSubscriberGroupPredicate(campaignDTO.getId());
        Iterable<CampaignSubscriberGroupEntity> campaignSubscriberGroupEntities = campaignSubscriberGroupDao.findAll(getCampaignSubscriberGroupPredicate);
        campaignSubscriberGroupDao.delete(campaignSubscriberGroupEntities);
    }

    private Predicate buildCampaignSubscriberGroupPredicate(Long campaignId) {
        if (campaignId != null) {
            CampaignEntity campaignEntity = campaignDao.findOne(campaignId);

            QCampaignSubscriberGroupEntity qCampaignSubscriberGroupEntity = QCampaignSubscriberGroupEntity.campaignSubscriberGroupEntity;

            return qCampaignSubscriberGroupEntity.campaignEntity.eq(campaignEntity);

        }
        return null;
    }

    private void saveCampaignSubcriberGroup(CampaignDTO campaignDTO, CampaignEntity campaignEntity) {
        for (Long subscriberGroupId : campaignDTO.getSubscriberGroupIds()) {
            SubscriberGroupEntity subscriberGroupEntity = subscriberGroupDao.findOne(subscriberGroupId);

            CampaignSubscriberGroupEntity campaignSubscriberGroupEntity = new CampaignSubscriberGroupEntity();
            campaignSubscriberGroupEntity.setCampaignEntity(campaignEntity);
            campaignSubscriberGroupEntity.setSubscriberGroupEntity(subscriberGroupEntity);
            campaignSubscriberGroupDao.save(campaignSubscriberGroupEntity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignDTO getCampaign(Long campaignId) {

        CampaignDTO campaignDTO = campaignConverter.convertToCampaignDTO(campaignDao.findOne(campaignId));

        assignSubscriberGroupIdToDTO(campaignDTO, campaignDTO);

        return campaignDTO;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCampaign(Long campaignId) {

        campaignDao.delete(campaignId);
    }
}
