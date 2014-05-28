package com.equivi.demailer.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class EmailCollectorController {
    @RequestMapping(value = "/main/emailcollector", method = RequestMethod.GET)
    public String getEmailCollectorPage() {
        return "emailCollectorPage";
    }
}
