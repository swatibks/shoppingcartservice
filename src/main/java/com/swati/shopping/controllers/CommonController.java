package com.swati.shopping.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.swati.shopping.models.data.Categories;
import com.swati.shopping.models.data.Page;
import com.swati.shopping.models.repos.CategoriesRepository;
import com.swati.shopping.models.repos.PageRepository;

@ControllerAdvice
public class CommonController {

	@Autowired
	private PageRepository pageRepo;
	
	@Autowired
	private CategoriesRepository categoryRepo;
	
	@ModelAttribute
	public void shareData(Model model, Principal principal) {
		
		if(principal != null) {
			model.addAttribute("principal", principal.getName());
		}
		List<Page> allPages = pageRepo.findAllByOrderBySortingAsc();
		List<Categories> categories = categoryRepo.findAll();
		model.addAttribute("allpages", allPages);
		model.addAttribute("categories", categories);
	}
}
