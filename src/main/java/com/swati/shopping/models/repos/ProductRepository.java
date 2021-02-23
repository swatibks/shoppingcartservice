package com.swati.shopping.models.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.swati.shopping.models.data.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findByName(String name);

	Product findBySlugAndIdNot(String slug, int id);
	
	Page<Product> findAll(Pageable pageable);

	Page<Product> findAllByCategoryId(String categoryId, Pageable pageable);

	long countByCategoryId(String categoryId);

}
