package com.equivi.demailer.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.equivi.demailer.dto.emailer.EmailCollector;
import com.equivi.demailer.dto.emailer.EmailCollectorMessage;
import com.equivi.demailer.service.emailcollector.EmailCollectorService;
import com.google.common.collect.Lists;


@Controller
@RequestMapping("/main/emailcollector")
public class EmailCollectorController {
    private static final String EMAIL_COLLECTOR_PAGE = "emailCollectorPage";
    
    @Autowired
    private EmailCollectorService emailCollectorService;
    
	@RequestMapping(value = "/new", method = RequestMethod.GET)
    public String loadNewPage(Model model) {
		model.addAttribute("collector", new EmailCollector());
		
        return EMAIL_COLLECTOR_PAGE;
    }
    
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public String collectEmails(@ModelAttribute("collector") EmailCollector collector, Model map) throws Exception {
    	map.addAttribute("resultList", getEmailCollectorResults(collector));
    	
    	return EMAIL_COLLECTOR_PAGE;
    }
    
    private List<EmailCollector> getEmailCollectorResults(EmailCollector collector) throws Exception {
        List<EmailCollector> emailCollectorsList = Lists.newArrayList();
        return emailCollectorsList;
    }
    
    @RequestMapping(value = "async/{site}/begin", method = RequestMethod.GET)
    public @ResponseBody String start(@PathVariable String site) throws Exception { 
    	emailCollectorService.subscribe("http://" + site);
    	return "OK";
    }
    
    @RequestMapping(value = "async/update", method = RequestMethod.GET)
    public @ResponseBody DeferredResult<EmailCollectorMessage> getUpdate() {
    	final DeferredResult<EmailCollectorMessage> result = new DeferredResult<>();
    	emailCollectorService.getUpdate(result);
        return result;
    	
    }
}
