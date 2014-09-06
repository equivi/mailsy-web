package com.equivi.mailsy.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ReportController {

    @RequestMapping(value = "/main/merchant/report", method = RequestMethod.GET)
    public String getReportPage() {
        return "reportPage";
    }
}
