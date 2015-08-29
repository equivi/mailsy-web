package com.equivi.mailsy.service.user;


import com.equivi.mailsy.data.dao.UserDao;
import com.equivi.mailsy.data.entity.QUserEntity;
import com.equivi.mailsy.data.entity.UserEntity;
import com.equivi.mailsy.data.entity.UserRole;
import com.equivi.mailsy.data.entity.UserStatus;
import com.equivi.mailsy.dto.login.UserLoginDTO;
import com.equivi.mailsy.dto.user.UserRequestDTO;
import com.equivi.mailsy.service.authentication.AuthenticationPredicate;
import com.equivi.mailsy.service.encryption.EncryptionService;
import com.equivi.mailsy.service.encryption.PasswordService;
import com.equivi.mailsy.service.encryption.PasswordUtil;
import com.equivi.mailsy.service.exception.AuthenticationFailureException;
import com.equivi.mailsy.service.exception.InvalidDataException;
import com.equivi.mailsy.service.user.validator.UserServiceValidator;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(value = "transactionManager")
public class UserServiceImpl implements UserService {

    public static final int MAX_FAILED_LOGIN = 3;
    static final String INVALID_USER_NAME_OR_PASSWORD = "Bad user name or password entered! Pls try again";
    private static final String USER_STATUS_INACTIVE = "Unable to login,user is inactive";
    private static final String USER_STATUS_LOCKED = "Unable to login,user is locked";
    @Resource
    private UserDao userDao;
    @Resource
    private PasswordService passwordService;
    @Resource
    private AuthenticationPredicate authenticationPredicate;
    @Resource
    private EncryptionService encryptionService;
    @Resource
    private UserMailService userMailService;
    @Resource
    private UserServiceValidator userServiceValidator;

    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> listUser(Map<UserSearchFilter, String> userSearchFilterStringMap, int page, int maxRecords) {

        Pageable pageable = getPageable(page - 1, maxRecords);
        Predicate userQueryPredicate = getUserQueryPredicate(userSearchFilterStringMap);

        Page requestedPage = userDao.findAll(userQueryPredicate, pageable);

        return requestedPage;
    }

    private Predicate getUserQueryPredicate(Map<UserSearchFilter, String> filterMap) {

        QUserEntity qUserEntity = QUserEntity.userEntity;
        BooleanBuilder booleanMerchantPredicateBuilder = new BooleanBuilder();

        if (filterMap.get(UserSearchFilter.USERNAME) != null) {
            booleanMerchantPredicateBuilder.or(qUserEntity.firstName.append(qUserEntity.lastName).like("%" + filterMap.get(UserSearchFilter.USERNAME) + "%"));

        }
        if (filterMap.get(UserSearchFilter.EMAIL_ADDRESS) != null) {
            booleanMerchantPredicateBuilder.and(qUserEntity.userName.like("%" + filterMap.get(UserSearchFilter.EMAIL_ADDRESS) + "%"));
        }

        return booleanMerchantPredicateBuilder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(Long userId) {
        return userDao.findOne(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByUserName(String userName) {
        Iterable<UserEntity> userEntities = userDao.findAll(authenticationPredicate.getUserByUserName(userName));

        List<UserEntity> userEntityList = constructList(userEntities);

        if (userEntityList.size() > 0) {
            return userEntityList.get(0);
        }

        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public Long createUser(UserRequestDTO userRequestDTO) {
        userServiceValidator.validate(userRequestDTO);

        UserEntity userEntity = convertAndFillInValueToUserEntity(userRequestDTO);

        userEntity = userDao.save(userEntity);

        //Send email to user
        userMailService.sendEmailForUserCreation(userRequestDTO);


        return userEntity.getId();
    }

    private UserEntity convertAndFillInValueToUserEntity(UserRequestDTO userRequestDTO) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUserName(userRequestDTO.getEmailAddress());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setUserStatus(UserStatus.ACTIVE);
        userEntity.setPhoneNo(userRequestDTO.getPhoneNo());
        userEntity.setUserRole(UserRole.getUserRoleById(userRequestDTO.getUserRole()));


        //Generate Password
        if (userRequestDTO.isGeneratePassword()) {
            String generatedPassword = passwordService.generateRandomPassword(PasswordUtil.PASSWORD_MIN_LENGTH);
            userRequestDTO.setPassword(generatedPassword);

            //Set to UserEntity
            userEntity.setPassword(encryptionService.encrypt(generatedPassword));
            userEntity.setResetPasswordRequired(true);
        } else {

            if (!StringUtils.isBlank(userRequestDTO.getPassword())) {
                checkPasswordValidity(userRequestDTO);
                userEntity.setPassword(encryptionService.encrypt(userRequestDTO.getPassword()));
            } else {
                throw new InvalidDataException("password.empty");
            }
        }
        return userEntity;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateUser(UserRequestDTO userRequestDTO) {
        userServiceValidator.validateUpdate(userRequestDTO);

        UserEntity userEntity = userDao.findOne(userRequestDTO.getUserId());

        if (userEntity == null) {
            throw new InvalidDataException("Invalid User Id");
        }

        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setUserStatus(UserStatus.getUserStatusById(userRequestDTO.getUserStatus()));
        userEntity.setPhoneNo(userRequestDTO.getPhoneNo());
        userEntity.setUserRole(UserRole.getUserRoleById(userRequestDTO.getUserRole()));

        boolean sendMail = false;
        if (userRequestDTO.isGeneratePassword() || !StringUtils.isBlank(userRequestDTO.getPassword())) {
            if (userRequestDTO.isGeneratePassword()) {
                String generatedPassword = passwordService.generateRandomPassword(PasswordUtil.PASSWORD_MIN_LENGTH);
                userRequestDTO.setPassword(generatedPassword);

                //Set to UserEntity
                userEntity.setPassword(encryptionService.encrypt(generatedPassword));
                userEntity.setResetPasswordRequired(true);

                sendMail = true;

            } else {
                if (!StringUtils.isBlank(userRequestDTO.getPassword()) && userServiceValidator.isPasswordDifferentWithOriginal(userEntity.getPassword(), userRequestDTO.getPassword())) {
                    checkPasswordValidity(userRequestDTO);
                    userEntity.setPassword(encryptionService.encrypt(userRequestDTO.getPassword()));

                    sendMail = true;
                }
            }
        }

        userDao.save(userEntity);

        if (sendMail) {
            userMailService.sendEmailForPasswordUpdate(userRequestDTO);
        }
    }


    @Override
    @Transactional(readOnly = false)
    public void updateFailedLoginRetry(String userName) {
        UserEntity userEntity = getUserByUserName(userName);
        if (userEntity != null) {
            Integer failedLogin = userEntity.getFailedLoginCounter();
            if (failedLogin == null) {
                failedLogin = 0;
            }
            failedLogin++;

            if (failedLogin == MAX_FAILED_LOGIN) {
                userEntity.setUserStatus(UserStatus.LOCKED);
            }
            userEntity.setFailedLoginCounter(failedLogin);
            userDao.save(userEntity);
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void resetFailedLoginRetry(String userName) {
        UserEntity userEntity = getUserByUserName(userName);
        if (userEntity != null) {
            userEntity.setFailedLoginCounter(0);
            userDao.save(userEntity);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword, boolean sendEmail) {

        UserEntity userEntity = userDao.findOne(userId);

        userServiceValidator.validateChangePassword(userEntity, oldPassword, newPassword, confirmPassword);

        userEntity.setResetPasswordRequired(false);
        userEntity.setPassword(encryptionService.encrypt(newPassword));

        userDao.save(userEntity);

        if (sendEmail) {
            //Send email to user
            UserRequestDTO userRequestDTO = new UserRequestDTO();
            userRequestDTO.setFirstName(userEntity.getFirstName());
            userRequestDTO.setLastName(userEntity.getLastName());
            userRequestDTO.setEmailAddress(userEntity.getUserName());
            userRequestDTO.setPassword(newPassword);

            userMailService.sendEmailForPasswordUpdate(userRequestDTO);
        }
    }

    @Override
    public UserLoginDTO getUserLoginDTO(final String userName) {

        Iterable<UserEntity> userEntityList = userDao.findAll(authenticationPredicate.getUserByUserName(userName));

        if (userEntityList == null || !userEntityList.iterator().hasNext()) {
            throw new AuthenticationFailureException(INVALID_USER_NAME_OR_PASSWORD);
        }

        List<UserEntity> userEntities = constructList(userEntityList);

        UserEntity userEntity = userEntities.get(0);

        if (userEntity.getUserStatus().equals(UserStatus.INACTIVE)) {
            throw new AuthenticationFailureException(USER_STATUS_INACTIVE);
        }

        if (userEntity.getUserStatus().equals(UserStatus.LOCKED)) {
            throw new AuthenticationFailureException(USER_STATUS_LOCKED);
        }

        return buildUserLoginResponseDTO(userEntities.get(0));
    }

    private UserLoginDTO buildUserLoginResponseDTO(UserEntity userEntity) {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserId(userEntity.getId());
        userLoginDTO.setUserName(userEntity.getUserName());
        userLoginDTO.setFirstName(userEntity.getFirstName());
        userLoginDTO.setLastName(userEntity.getLastName());
        userLoginDTO.setResetPasswordRequired(userEntity.isResetPasswordRequired());
        userLoginDTO.setUserRole(userEntity.getUserRole());
        userLoginDTO.setUserStatus(userEntity.getUserStatus());

        return userLoginDTO;
    }

    @Override
    @Transactional(readOnly = false)
    public void forgotPassword(String emailAddress) {

        //TODO: put this to validator
        if (StringUtils.isBlank(emailAddress)) {
            throw new InvalidDataException("label.forget_password.email_is_mandatory");
        }

        UserEntity userEntity = getUserByUserName(emailAddress);
        if (userEntity == null) {
            throw new InvalidDataException("invalid.email.address");
        }

        String password = passwordService.generateRandomPassword(PasswordUtil.PASSWORD_MIN_LENGTH).toString();
        userEntity.setPassword(encryptionService.encrypt(password));
        userEntity.setResetPasswordRequired(true);
        if (userEntity.getUserStatus().equals(UserStatus.LOCKED)) {
            userEntity.setUserStatus(UserStatus.ACTIVE);
        }
        userDao.save(userEntity);


        setUserRequestDTOForForgetPassword(userEntity, password);

    }

    private void setUserRequestDTOForForgetPassword(UserEntity userEntity, String password) {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName(userEntity.getFirstName());
        userRequestDTO.setLastName(userEntity.getLastName());
        userRequestDTO.setEmailAddress(userEntity.getUserName());
        userRequestDTO.setPassword(password);


        userMailService.sendEmailForForgetPassword(userRequestDTO);
    }


    private void checkPasswordValidity(UserRequestDTO userRequestDTO) {
        String fullName = userRequestDTO.getFirstName() + " " + userRequestDTO.getLastName();
        passwordService.checkPasswordValidation(userRequestDTO.getPassword(), fullName, userRequestDTO.getEmailAddress(), PasswordUtil.PASSWORD_MIN_LENGTH);
    }

    private Pageable getPageable(int page, int maxRecords) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(page, maxRecords, sort);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteUser(Long userId) {
        if (isSuperUser(userId)) {
            throw new InvalidDataException("failed.to.delete.super.admin");
        }
        userDao.delete(userId);
    }

    private boolean isSuperUser(Long userId) {
        return userId == 1;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getMerchantIDList(Long userId) {

        Set<String> midList = new HashSet<>();
        return midList;
    }

    private List<UserEntity> constructList(Iterable<UserEntity> users) {
        List<UserEntity> list = new ArrayList<>();
        for (UserEntity userEntity : users) {
            list.add(userEntity);
        }
        return list;
    }
}
