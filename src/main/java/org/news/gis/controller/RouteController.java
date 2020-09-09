package org.news.gis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RouteController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
}
