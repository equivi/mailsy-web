package com.equivi.mailsy.web.controller;


import com.equivi.mailsy.service.mailgun.MailgunService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Controller
public class UnsubscribeController {

    static final String UNSUBSCRIBED_PAGE = "unsubscribedPage";

    static final String UNSUBSCRIBED_PAGE_NOTIFICATION = "unsubscribeNotificationPage";


    @Resource(name = "mailgunRestTemplateEmailService")
    private MailgunService mailgunJerseyService;

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
    public String goToUnsubscribedPage(@RequestParam String emailToUnsubscribe, @RequestParam String domainName, Model model) {

        model.addAttribute("emailToUnsubscribe", emailToUnsubscribe);
        model.addAttribute("domainName", domainName);

        return UNSUBSCRIBED_PAGE;
    }

    @RequestMapping(value = "/unsubscribe/unsubscribeEmailAddress", method = RequestMethod.GET)
    public String sendUnsubscribeRequest(@RequestParam String domainName, @RequestParam String emailToUnsubscribe, Model model) {
        mailgunJerseyService.registerUnsubscribe(domainName, emailToUnsubscribe);

        model.addAttribute("emailToUnsubscribe", emailToUnsubscribe);
        return UNSUBSCRIBED_PAGE_NOTIFICATION;
    }
}
