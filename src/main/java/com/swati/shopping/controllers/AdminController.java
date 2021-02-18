package com.swati.shopping.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.swati.shopping.models.data.Page;
import com.swati.shopping.models.repos.PageRepository;

@Controller
@RequestMapping("/admin/pages")
public class AdminController {
	
	@Autowired
	private PageRepository pageRepo;

	/* public AdminController(PageRepository pageRepo) {
		this.pageRepo = pageRepo;		
	}*/
	
	@GetMapping
	public String index(Model model) {
		List<Page> pages = pageRepo.findAll();
		model.addAttribute("pages", pages);
		return "admin/pages/index";
	}
	
	@GetMapping("/add")
	public String addpages(@ModelAttribute Page page) {
		//model.addAttribute("page", new Page());		
		return "admin/pages/addpage";
	}
	
	@PostMapping("/add")
	public String addpages(@Valid Page page, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		
		if(bindingResult.hasErrors()) {			
			return "admin/pages/addpage";
		}
		
		redirectAttributes.addFlashAttribute("message", "Page added");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		String slug = page.getSlug() == "" ? page.getTitle().toLowerCase().replaceAll(" ", "-") : page.getSlug().toLowerCase().replaceAll(" ", "-");
		Page slugExists = pageRepo.findBySlug(slug);
		if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message", "Slug exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("page", page);
		}else {
			page.setSlug(slug);
			page.setSorting(100);	
			pageRepo.save(page);
		}
		
		return "redirect:/admin/pages/add";
	}
	
	@GetMapping("/edit/{id}")
	public String editpages(@PathVariable int id, Model model) {
		Optional<Page> optionalPage = pageRepo.findById(id);
		Page page = optionalPage.get();
		model.addAttribute("page", page);
		return "admin/pages/editpage";
	}
	
	@PostMapping("/edit")
	public String editpages(@Valid Page page, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

		if(bindingResult.hasErrors()) {			
			return "admin/pages/editpage";
		}
		
		redirectAttributes.addFlashAttribute("message", "Page edited");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		String slug = page.getSlug() == "" ? page.getTitle().toLowerCase().replaceAll(" ", "-") : page.getSlug().toLowerCase().replaceAll(" ", "-");
		Page slugExists = pageRepo.findBySlugAndIdNot(slug, page.getId());
		if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message", "Slug exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("page", page);
		}else {
			page.setSlug(slug);
			pageRepo.save(page);
		}
		
		return "redirect:/admin/pages/edit/" + page.getId();
	}
	
	@GetMapping("/delete/{id}")
	public String deletepages(@PathVariable int id, RedirectAttributes redirectAttributes) {		
		pageRepo.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Delete successful");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/pages";
	}
	
	@PostMapping("/reorder")
	public @ResponseStatus String reorderpages(@RequestParam("id[]") int[] id) {
		int count = 1;
		Page page;
		for(int pageId : id) {
			page = pageRepo.getOne(pageId);
			page.setSorting(count);
			pageRepo.save(page);
			count++;			
		}
		return "ok";
	}
}
