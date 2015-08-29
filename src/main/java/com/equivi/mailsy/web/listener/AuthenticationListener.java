package com.equivi.mailsy.web.listener;


import com.equivi.mailsy.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class AuthenticationListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationListener.class);

    @Resource
    private UserService userService;

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent appEvent) {
        if (appEvent instanceof AuthenticationSuccessEvent) {

            // add code here to handle successful login event
            // Add event to system logger for reporting purpose later
            // Reset Failed Login
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) appEvent.getSource();
            userService.resetFailedLoginRetry(usernamePasswordAuthenticationToken.getName());
        }

        if (appEvent instanceof AuthenticationFailureBadCredentialsEvent) {
            // add code here to handle unsuccessful login event
            // for example, counting the number of login failure attempts and storing it in db
            // this count can be used to lock or disable any user account as per business requirements
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) appEvent.getSource();
            userService.updateFailedLoginRetry((String) usernamePasswordAuthenticationToken.getPrincipal());
        }
    }


}
