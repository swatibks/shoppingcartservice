package com.swati.shopping.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.swati.shopping.models.data.Page;
import com.swati.shopping.models.repos.PageRepository;

@Controller
@RequestMapping("/")
public class AllPagesController {
	
	@Autowired
	private PageRepository pageRepo;	
	
	@GetMapping
	public String showHome(Model model) {
		Page page = pageRepo.findBySlug("home");
		model.addAttribute("page", page);
		return "allpages";
	}
	
	@GetMapping("/{slug}")
	public String showHome(@PathVariable("slug") String slug, Model model) {
		
		Page page = pageRepo.findBySlug(slug);		
				
		if(page == null) {
			return "redirect:/";
		}
		
		model.addAttribute("page", page);
		return "allpages";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	

}
