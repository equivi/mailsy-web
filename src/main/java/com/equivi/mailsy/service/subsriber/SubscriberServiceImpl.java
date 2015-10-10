package com.equivi.mailsy.service.subsriber;

import com.equivi.mailsy.data.dao.SubscriberContactDao;
import com.equivi.mailsy.data.dao.SubscriberGroupDao;
import com.equivi.mailsy.data.entity.ContactEntity;
import com.equivi.mailsy.data.entity.QSubscriberContactEntity;
import com.equivi.mailsy.data.entity.SubscriberContactEntity;
import com.equivi.mailsy.data.entity.SubscriberGroupEntity;
import com.equivi.mailsy.dto.contact.ContactDTO;
import com.equivi.mailsy.service.contact.ContactConverter;
import com.equivi.mailsy.service.contact.ContactManagementService;
import com.google.common.collect.Lists;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
public class SubscriberServiceImpl implements SubscriberService {

    @Resource
    private SubscriberContactDao subscriberContactDao;

    @Resource
    private SubscriberGroupDao subscriberGroupDao;

    @Resource
    private ContactManagementService contactManagementService;

    @Resource
    private ContactConverter contactConverter;

    @Override
    public Page<SubscriberContactEntity> getSubscriberEntityPageBySubscriberGroupId(Long subscriberGroupId, int pageNumber, int maxRecords) {
        Pageable pageable = getPageable(pageNumber - 1, maxRecords);

        Predicate subscribeGroupQueryPredicate = getSubscriberContactPredicate(subscriberGroupId);

        Page<SubscriberContactEntity> requestedPage = subscriberContactDao.findAll(subscribeGroupQueryPredicate, pageable);
        return requestedPage;
    }

    @Override
    public List<SubscriberContactEntity> getSubscriberEntityPageBySubscriberGroupId(Long subscriberGroupId) {
        Predicate subscribeGroupQueryPredicate = getSubscriberContactPredicate(subscriberGroupId);

        return Lists.newArrayList(subscriberContactDao.findAll(subscribeGroupQueryPredicate));
    }

    @Override
    public List<ContactEntity> getSubscriberEntityListBySubscriberGroupId(Long subscriberGroupId) {
        Predicate subscribeGroupQueryPredicate = getSubscriberPredicate(subscriberGroupId);

        List<SubscriberContactEntity> subscriberContactEntities = Lists.newArrayList(subscriberContactDao.findAll(subscribeGroupQueryPredicate));

        List<ContactEntity> contactEntityList = new ArrayList<>();
        for (SubscriberContactEntity subscriberContactEntity : subscriberContactEntities) {
            contactEntityList.add(subscriberContactEntity.getContactEntity());
        }
        return contactEntityList;
    }

    @Override
    public void addSubscriberList(Long subscriberGroupId, List<ContactDTO> contactDTOList) {
        SubscriberGroupEntity subscriberGroupEntity = subscriberGroupDao.findOne(subscriberGroupId);

        //Delete all subscriber group entity under this group id
        subscriberContactDao.delete(getSubscriberContactEntityList(subscriberGroupId));

        if (contactDTOList != null && !contactDTOList.isEmpty()) {
            for (ContactDTO contactDTO : contactDTOList) {
                if (StringUtils.isNotEmpty(contactDTO.getEmailAddress())) {
                    ContactEntity contactEntity = contactManagementService.getContactEntityByEmailAddress(contactDTO.getEmailAddress());
                    if (contactEntity == null) {
                        contactEntity = contactManagementService.saveContactEntity(contactDTO);
                    } else {
                        contactEntity = contactConverter.convertToEntity(contactEntity, contactDTO);
                        contactManagementService.saveContactEntity(contactEntity);
                    }
                    SubscriberContactEntity subscriberContactEntity = new SubscriberContactEntity();
                    subscriberContactEntity.setContactEntity(contactEntity);
                    subscriberContactEntity.setSubscriberGroupEntity(subscriberGroupEntity);

                    subscriberContactDao.save(subscriberContactEntity);

                }
            }
        }
    }

    private List<SubscriberContactEntity> getSubscriberContactEntityList(Long subscriberGroupId) {
        Predicate getSubscribeGroupEntityPredicate = getSubscriberPredicate(subscriberGroupId);

        List<SubscriberContactEntity> subscriberContactEntityList = Lists.newArrayList(subscriberContactDao.findAll(getSubscribeGroupEntityPredicate));


        return subscriberContactEntityList;

    }

    private Predicate getSubscriberContactPredicate(Long subscriberGroupId) {
        QSubscriberContactEntity qSubscriberContactEntity = QSubscriberContactEntity.subscriberContactEntity;

        SubscriberGroupEntity subscriberGroupEntity = subscriberGroupDao.findOne(subscriberGroupId);

        return qSubscriberContactEntity.subscriberGroupEntity.eq(subscriberGroupEntity);
    }

    private Predicate getSubscriberPredicate(Long subscriberGroupId) {
        QSubscriberContactEntity qSubscriberContactEntity = QSubscriberContactEntity.subscriberContactEntity;

        SubscriberGroupEntity subscriberGroupEntity = subscriberGroupDao.findOne(subscriberGroupId);

        return qSubscriberContactEntity.subscriberGroupEntity.eq(subscriberGroupEntity);
    }

    private Pageable getPageable(int page, int maxRecords) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(page, maxRecords, sort);
    }
}
