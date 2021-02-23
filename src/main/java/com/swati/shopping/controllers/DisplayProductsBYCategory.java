package com.swati.shopping.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.swati.shopping.models.data.Categories;
import com.swati.shopping.models.data.Product;
import com.swati.shopping.models.repos.CategoriesRepository;
import com.swati.shopping.models.repos.ProductRepository;

@Controller
@RequestMapping("/category")
public class DisplayProductsBYCategory {
	
	@Autowired
	private CategoriesRepository categoriesRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@GetMapping("/{slug}")
	public String productsDisplayByCategory(@PathVariable String slug, Model model, @RequestParam(value="page", required = false) Integer p) {
		
		int perPageCount = 6;
		int pageRequested = (p != null) ? p : 0;		
		Pageable pageable = PageRequest.of(pageRequested, perPageCount);
		long count = 0;
		
		if(slug.equals("all")){				
			Page<Product> products= productRepo.findAll(pageable);	
			model.addAttribute("products", products);
			count = productRepo.count();
		}else {
			Categories currentCategory = categoriesRepo.findBySlug(slug);
			
			if(currentCategory == null) {
				return "redirect:/";
			}
			
			int categoryId = currentCategory.getId();
			String categoryName = currentCategory.getName();
			Page<Product> products= productRepo.findAllByCategoryId(Integer.toString(categoryId), pageable);
			count = productRepo.countByCategoryId(Integer.toString(categoryId));
			
			model.addAttribute("products", products);
			model.addAttribute("categoryName", categoryName);
		}
		
		model.addAttribute("count", count);		
		
		double totalPages = Math.ceil((double) count / (double) perPageCount);
		
		model.addAttribute("totalPages", (int) totalPages);
		model.addAttribute("count", count);
		model.addAttribute("perPageCount", perPageCount);
		model.addAttribute("pageRequested", pageRequested);
		
		return "products";
	}
}
