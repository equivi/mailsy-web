//package com.equivi.mailsy.service.authentication;
//
//import com.equivi.mailsy.data.dao.UserDao;
//import com.equivi.mailsy.data.entity.UserEntity;
//import com.equivi.mailsy.data.entity.UserRole;
//import com.equivi.mailsy.data.entity.UserStatus;
//import com.equivi.mailsy.service.exception.AuthenticationFailureException;
//import com.mysema.query.types.Predicate;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//
//public class AuthenticationServiceImplTest {
//
//    private static final String USERNAME = "USER";
//    private static final String PASSWORD = "PASSWORD";
//    @Rule
//    public ExpectedException expectedException = ExpectedException.none();
//    @Mock
//    private UserDao userDao;
//    @Mock
//    private AuthenticationPredicate authenticationPredicate;
//    @Mock
//    private Predicate mockPredicate;
//    @InjectMocks
//    private AuthenticationServiceImpl authenticationService;
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testAuthenticateUserSuccess() throws Exception {
//
//        List<UserEntity> userEntities = prepareUserEntityList();
//
//        //Given
//        when(authenticationPredicate.getUserByUserName(anyString())).thenReturn(mockPredicate);
//
//        when(userDao.findAll(any(Predicate.class))).thenReturn(userEntities);
//
//        //When
//        authenticationService.getUser(USERNAME);
//
//        //Then
//        verify(userDao).findAll(mockPredicate);
//    }
//
//    @Test
//    public void testAuthenticationFailed() throws Exception {
//
//        //Then
//        expectedException.expect(AuthenticationFailureException.class);
//        expectedException.expectMessage(AuthenticationServiceImpl.INVALID_USER_NAME_OR_PASSWORD);
//
//        //Given
//        when(userDao.findAll(any(Predicate.class))).thenReturn(null);
//        when(authenticationPredicate.getUserByUserName(anyString())).thenReturn(mockPredicate);
//
//        //When
//        authenticationService.getUser(USERNAME);
//
//    }
//
//
//    @Test
//    public void testLoadUserByUserName() throws Exception {
//        List<UserEntity> userEntities = prepareUserEntityList();
//
//        //Given
//        when(userDao.findAll(any(Predicate.class))).thenReturn(userEntities);
//
//        when(authenticationPredicate.getUserByUserName(anyString())).thenReturn(mockPredicate);
//
//        //When
//        authenticationService.loadUserByUsername(USERNAME);
//
//        //Then
//        verify(userDao).findAll(mockPredicate);
//
//    }
//
//    private List<UserEntity> prepareUserEntityList() {
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUserName(USERNAME);
//        userEntity.setPassword(PASSWORD);
//        userEntity.setUserRole(UserRole.MAILSY_ADMIN);
//        userEntity.setUserStatus(UserStatus.ACTIVE);
//
//        List<UserEntity> userEntities = new ArrayList<>();
//        userEntities.add(userEntity);
//
//
//        return userEntities;
//    }
//}
