package com.equivi.mailsy.web.controller;


import com.equivi.mailsy.dto.login.UserLoginDTO;
import com.equivi.mailsy.service.user.UserService;
import com.equivi.mailsy.web.constant.PageConstant;
import com.equivi.mailsy.web.context.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@SessionAttributes("userLoggedIn")
public class LoginSecurityController {

    private static final Logger logger = LoggerFactory.getLogger(LoginSecurityController.class);
    private static final String WELCOME_PAGE = "welcomePage";
    private static final String RESET_PASSWORD = "forceChangePasswordPage";
    private static final String CHANGE_PASSWORD_SUCCESSFULLY = "change.password.succesfully.notification";

    @Resource
    private UserService userService;

    @Resource
    private SessionUtil sessionUtil;

    @RequestMapping(value = "/login-page", method = RequestMethod.GET)
    public String getLoginScreen(final Model model, final HttpServletRequest httpServletRequest) {

        String originPage = httpServletRequest.getParameter(PageConstant.ORIGINATED_PAGE.getPageName());
        if (!StringUtils.isBlank(originPage)) {
            if (originPage.equals(PageConstant.FORCE_CHANGE_PASSWORD_PAGE.getPageName()) ||
                    originPage.equals(PageConstant.CHANGE_PASSWORD_PAGE.getPageName())) {
                model.addAttribute("success_message", CHANGE_PASSWORD_SUCCESSFULLY);
            }
        }
        return "loginPage";
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView getMainMenu(@ModelAttribute ModelAndView modelAndView, HttpServletRequest request, Principal principal) {
        UserLoginDTO userLoginDTO = userService.getUserLoginDTO(principal.getName());

        if (userLoginDTO.isResetPasswordRequired()) {
            modelAndView.setViewName(RESET_PASSWORD);
        } else {
            modelAndView.setViewName(WELCOME_PAGE);
        }
        sessionUtil.setCurrentUser(request, userLoginDTO);
        return modelAndView;
    }


    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public String loginError(ModelMap model, Principal principal) {
        System.out.println("PRINCIPAL" + principal);
        model.addAttribute("error", "true");
        return "loginPage";
    }

}
