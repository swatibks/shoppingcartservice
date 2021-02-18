package com.swati.shopping.models.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swati.shopping.models.data.Page;

public interface PageRepository extends JpaRepository<Page, Integer> {
	
	Page findBySlug(String slug);

	Page findBySlugAndIdNot(String slug, int id);

}
