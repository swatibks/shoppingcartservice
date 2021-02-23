package com.swati.shopping.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.swati.shopping.models.data.Categories;
import com.swati.shopping.models.repos.CategoriesRepository;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoriesController {
	
	@Autowired
	private CategoriesRepository categoriesRepo;
	
	@GetMapping
	public String categoryHome(Model model) {
		List<Categories> categories = categoriesRepo.findAll();
		model.addAttribute("categories", categories);
		return "admin/categories/categoriesIndex";
	}
	
	@GetMapping("/addcategory")
	public String addcategory(@ModelAttribute Categories categories) {		
		return "admin/categories/addcategory";
	}
	
	@PostMapping("/addcategory")
	public String addcategory(@Valid Categories categories, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if(bindingResult.hasErrors()) {
			return "admin/categories/addcategory";
		}
		redirectAttributes.addFlashAttribute("message", "Category added");
		redirectAttributes.addFlashAttribute("alertClass" , "alert-success");
		
		String slug = categories.getName().toLowerCase().replaceAll(" ", "-");
		Categories slugExists = categoriesRepo.findBySlug(slug);
		if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message", "Category already exist, choose another");
			redirectAttributes.addFlashAttribute("alertClass" , "alert-danger");
			redirectAttributes.addFlashAttribute("categories", categories);
		}else {
			categories.setSlug(slug);
			categories.setSorting(100);
			categoriesRepo.save(categories);
		}
		
		return "redirect:/admin/categories/addcategory";		
	}
	
	@GetMapping("/editcategory/{id}")
	public String editcategory(@PathVariable int id, Model model) {
		Optional<Categories> categoriesOpt = categoriesRepo.findById(id);
		Categories categories = categoriesOpt.get();
		model.addAttribute("categories", categories);
		return "admin/categories/editcategory";
	}
	
	@PostMapping("/editcategory")
	public String editcategory(@Valid Categories categories, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if(bindingResult.hasErrors()) {
			return "admin/categories/editcategory";
		}
		redirectAttributes.addFlashAttribute("message", "Category edited");
		redirectAttributes.addFlashAttribute("alertClass" , "alert-success");
		
		String slug = categories.getName().toLowerCase().replaceAll(" ", "-");
		Categories slugExists = categoriesRepo.findBySlugAndIdNot(slug, categories.getId());
		if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message", "Category already exist, choose another");
			redirectAttributes.addFlashAttribute("alertClass" , "alert-danger");
			redirectAttributes.addFlashAttribute("categories", categories);
		}else {
			categories.setSlug(slug);
			categoriesRepo.save(categories);
		}
		
		return "redirect:/admin/categories/editcategory/" + categories.getId();		
	}
	
	@GetMapping("/deletecategory/{id}")
	public String deleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
		categoriesRepo.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Delete successful");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/categories";
	}
	
	@PostMapping("/reorder")
	public @ResponseStatus String reorderpages(@RequestParam("id[]") int[] id) {
		int count = 1;
		Categories categories;
		for(int categoryId : id) {
			categories = categoriesRepo.getOne(categoryId);
			categories.setSorting(count);
			categoriesRepo.save(categories);
			count++;			
		}
		return "ok";
	}	
}
