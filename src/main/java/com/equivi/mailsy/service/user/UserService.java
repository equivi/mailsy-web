package com.equivi.mailsy.service.user;


import com.equivi.mailsy.data.entity.UserEntity;
import com.equivi.mailsy.dto.login.UserLoginDTO;
import com.equivi.mailsy.dto.user.UserRequestDTO;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Set;

public interface UserService {


    /**
     * @param page
     * @param maxRecords
     * @return
     */
    Page<UserEntity> listUser(Map<UserSearchFilter, String> userSearchFilter, int page, int maxRecords);


    /**
     * @param userId
     * @return UserEntity
     */
    UserEntity getUser(Long userId);

    /**
     * @param userName
     * @return UserLoginDTO
     */
    UserLoginDTO getUserLoginDTO(final String userName);

    /**
     * @param userName
     * @return
     */
    UserEntity getUserByUserName(String userName);


    /**
     * @param userRequestDTO
     * @return Long
     */
    Long createUser(UserRequestDTO userRequestDTO);


    /**
     * @param userRequestDTO
     */
    void updateUser(UserRequestDTO userRequestDTO);


    /**
     * @param userName
     */
    void updateFailedLoginRetry(String userName);

    /**
     * @param userName
     */
    void resetFailedLoginRetry(String userName);

    /**
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @param sendEmail
     */
    void changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword, boolean sendEmail);

    /**
     * @param emailAddress
     */
    void forgotPassword(String emailAddress);

    /**
     * @param adminUserId
     */
    void deleteUser(Long adminUserId);


    /**
     * @param userId
     * @return Set<String>
     */
    Set<String> getMerchantIDList(Long userId);


}
