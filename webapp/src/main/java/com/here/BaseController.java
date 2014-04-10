package com.here;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
 
@Controller
@RequestMapping("/")
public class BaseController {
 
	@RequestMapping(value="/welcome", method = RequestMethod.GET,  produces = "text/plain;charset=UTF-8")
	public String welcome(ModelMap model) {
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - welcome() "+System.currentTimeMillis());
 
		//Spring uses InternalResourceViewResolver and return back reply.jsp
		return "reply";
	}

}