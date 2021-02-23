package com.swati.shopping.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.swati.shopping.models.data.Categories;
import com.swati.shopping.models.data.Product;
import com.swati.shopping.models.repos.CategoriesRepository;
import com.swati.shopping.models.repos.ProductRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private CategoriesRepository categoriesRepo;
	
	@GetMapping
	public String productIndex(Model model, @RequestParam(value="page", required = false) Integer p) {
		
		List<Categories> categories = categoriesRepo.findAll();
		Map<Integer, String> catMap = new HashMap<>();
		for(Categories cat : categories) {
			catMap.put(cat.getId(), cat.getName());
		}
		
		int perPageCount = 6;
		int pageRequested = (p != null) ? p : 0;
		Long count = productRepo.count();
		double totalPages = Math.ceil((double) count / (double) perPageCount);
		
		model.addAttribute("totalPages", (int) totalPages);
		model.addAttribute("count", count);
		model.addAttribute("perPageCount", perPageCount);
		model.addAttribute("pageRequested", pageRequested);
		
		Pageable pageable = PageRequest.of(pageRequested, perPageCount);
		Page<Product> products= productRepo.findAll(pageable);
		//List<Product> products = productRepo.findAll();
		model.addAttribute("products", products);
		model.addAttribute("catMap", catMap);		
		
		return "admin/product/productindex";
	}
	
	
	@GetMapping("/addproduct")
	public String addProduct(Product product, Model model) {
		List<Categories> categories = categoriesRepo.findAll();
		model.addAttribute("categories", categories);
		return "admin/product/addproduct";
	}
		
	@PostMapping("/addproduct")
	public String addproduct(@Valid Product product
			    ,BindingResult bindingResult
				, MultipartFile file
			    , RedirectAttributes redirectAttributes
				, Model model) throws IOException{
		
		List<Categories> categories = categoriesRepo.findAll();
		
		if(bindingResult.hasErrors()) {		
			model.addAttribute("categories", categories);
			return "admin/product/addproduct";
		}
		
		boolean fileOk = false;
		byte[] bytes = file.getBytes();
		String filename = file.getOriginalFilename();
		Path path = Paths.get("src/main/resources/static/media/" + filename);
		
		if(filename.endsWith("jpg") || filename.endsWith("png")) {
			fileOk = true;
		}
		
		redirectAttributes.addFlashAttribute("message", "Product added");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		String slug = product.getName().toLowerCase().replaceAll(" ", "-");
		Product slugExists = productRepo.findByName(slug);
		
		if(!fileOk) {
			redirectAttributes.addFlashAttribute("message", "Image can be only jpg or png");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
		}else if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("product", product);
		}else {
			product.setSlug(slug);	
			product.setImage(filename);
			productRepo.save(product);
			Files.write(path, bytes);
		}
		
		return "redirect:/admin/products/addproduct";
	}
	
	@GetMapping("/editproduct/{id}")
	public String editProduct(@PathVariable int id, Model model) {
		List<Categories> categories = categoriesRepo.findAll();		
		Optional<Product> productOpt = productRepo.findById(id);
		Product product = productOpt.get();
		model.addAttribute("product", product);
		model.addAttribute("categories", categories);
		return "/admin/product/editproduct";
	}
	
	@PostMapping("/editproduct")
	public String editproduct(@Valid Product product
			    ,BindingResult bindingResult
				, MultipartFile file
			    , RedirectAttributes redirectAttributes
				, Model model) throws IOException{
		
		Product savedProduct = productRepo.getOne(product.getId());		
		
		List<Categories> categories = categoriesRepo.findAll();
		
		if(bindingResult.hasErrors()) {		
			model.addAttribute("categories", categories);
			model.addAttribute("productname", product);
			return "admin/product/editproduct";
		}
		
		byte[] bytes = file.getBytes();
		String filename = file.getOriginalFilename();
		Path path = Paths.get("src/main/resources/static/media/" + filename);
		
		boolean fileOk = false;
		
		if(!file.isEmpty() ) {
			if(filename.endsWith("jpg") || filename.endsWith("png")) {
				fileOk = true;
			}			
		}else {
			fileOk = true;
		}
		
		redirectAttributes.addFlashAttribute("message", "Product edited");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		String slug = product.getName().toLowerCase().replaceAll(" ", "-");
		Product slugExists = productRepo.findBySlugAndIdNot(slug, product.getId());
		
		if(!fileOk) {
			redirectAttributes.addFlashAttribute("message", "Image can be only jpg or png");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("product", product);
		}else if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("product", product);
		}else {
			
			product.setSlug(slug);	
			
			if(!file.isEmpty()) {
				Path path2 = Paths.get("src/main/resources/static/media/" + savedProduct.getImage());
				Files.delete(path2);
				product.setImage(filename);
				Files.write(path, bytes);				
			}else {
				product.setImage(savedProduct.getImage());
			}
			
			productRepo.save(product);			
		}
		
		return "redirect:/admin/products/editproduct/" + savedProduct.getId();
	}
	
	@GetMapping("/deleteproduct/{id}")
	public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) throws IOException {
		
		Product product = productRepo.getOne(id);
		Path path = Paths.get("src/main/resources/static/media/" + product.getImage());
	
		Files.delete(path);
		productRepo.deleteById(id);		
		
		redirectAttributes.addFlashAttribute("message", "Product Deleted");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/admin/products";
	}

}
