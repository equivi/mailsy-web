package com.equivi.mailsy.service.authentication;


import com.equivi.mailsy.data.dao.UserDao;
import com.equivi.mailsy.data.entity.UserEntity;
import com.equivi.mailsy.data.entity.UserRole;
import com.equivi.mailsy.data.entity.UserStatus;
import com.equivi.mailsy.dto.login.UserLoginDTO;
import com.equivi.mailsy.service.exception.AuthenticationFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(value = "transactionManager", readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {

    static final String INVALID_USER_NAME_OR_PASSWORD = "Bad user name or password entered! Pls try again";
    private static final String USER_STATUS_INACTIVE = "Unable to login,user is inactive";
    private static final String USER_STATUS_LOCKED = "Unable to login,user is locked";
    @Resource
    private UserDao userDao;
    @Resource
    private AuthenticationPredicate authenticationPredicate;

    /**
     * Wraps {@link String} roles to {@link SimpleGrantedAuthority} objects
     *
     * @param roles {@link String} of roles
     * @return list of granted authorities
     */
    public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    @Override
    public UserLoginDTO getUser(final String userName) {

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

    private List<UserEntity> constructList(Iterable<UserEntity> users) {
        List<UserEntity> list = new ArrayList<>();
        for (UserEntity userEntity : users) {
            list.add(userEntity);
        }
        return list;
    }

    /**
     * Overwrite Spring security method to loadUserByUserName
     *
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
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

        return new User(userEntity.getUserName(), userEntity.getPassword(), getAuthorities(userEntity.getUserRole()));
    }

    /**
     * Retrieves a collection of {@link GrantedAuthority} based on a numerical role
     *
     * @param userRole the numerical role
     * @return a collection of {@link GrantedAuthority
     */
    public Collection<? extends GrantedAuthority> getAuthorities(UserRole userRole) {
        List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(userRole));
        return authList;
    }

    /**
     * Converts a numerical role to an equivalent list of roles
     *
     * @param userRole the numerical role
     * @return list of roles as as a list of {@link String}
     */
    public List<String> getRoles(UserRole userRole) {
        List<String> roles = new ArrayList<>();

        if (userRole.equals(UserRole.DEMAILER_ADMIN)) {
            roles.add(UserRole.DEMAILER_ADMIN.getRoleDescription());
            roles.add(UserRole.DEMAILER_BASIC_USER.getRoleDescription());
            roles.add(UserRole.DEMAILER_PROFESSIONAL_USER.getRoleDescription());
            roles.add(UserRole.DEMAILER_ENTERPRISE_USER.getRoleDescription());
      } else {
            roles.add(userRole.getRoleDescription());
        }

        return roles;
    }
}
