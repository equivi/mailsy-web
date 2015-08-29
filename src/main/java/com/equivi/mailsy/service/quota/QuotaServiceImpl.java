package com.equivi.mailsy.service.quota;


import com.equivi.mailsy.data.dao.QuotaDao;
import com.equivi.mailsy.data.entity.QuotaEntity;
import com.equivi.mailsy.dto.quota.QuotaDTO;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;

@Service
public class QuotaServiceImpl implements QuotaService {
    private static final Logger logger = LoggerFactory.getLogger(QuotaServiceImpl.class);

    @Resource
    private QuotaDao quotaDao;

    @Override
    public QuotaDTO getQuota() {
        QuotaDTO quotaDTO = new QuotaDTO();
        QuotaEntity quotaEntity = quotaDao.findAll().get(0);

        try {
            PropertyUtils.copyProperties(quotaDTO, quotaEntity);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }

        return quotaDTO;
    }

    @Override
    @Transactional
    public QuotaDTO saveQuotaEntity(QuotaDTO quotaDTO) {
        QuotaEntity quotaEntity = quotaDao.findAll().get(0);

        if (quotaDTO.getCurrentEmailsVerified() > 0) {
            quotaEntity.setCurrentEmailsVerified(quotaEntity.getCurrentEmailsVerified() + quotaDTO.getCurrentEmailsVerified());
        }

        if (quotaDTO.getCurrentEmailsSent() > 0) {
            quotaEntity.setCurrentEmailsSent(quotaEntity.getCurrentEmailsSent() + quotaDTO.getCurrentEmailsSent());
        }

        QuotaEntity saved = quotaDao.save(quotaEntity);

        try {
            PropertyUtils.copyProperties(quotaDTO, saved);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }

        return quotaDTO;
    }

    @Override
    public Long getEmailVerifierRemainingLimit() {
        QuotaDTO quota = getQuota();
        long emailVerifyQuota = quota.getEmailVerifyQuota();
        long currentEmailsVerified = quota.getCurrentEmailsVerified();
        return (emailVerifyQuota - currentEmailsVerified);
    }
}
