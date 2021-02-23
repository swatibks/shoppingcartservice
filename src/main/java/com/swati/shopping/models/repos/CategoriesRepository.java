package com.swati.shopping.models.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swati.shopping.models.data.Categories;
import java.util.List;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
	Categories findBySlug(String slug);
	Categories findBySlugAndIdNot(String slug, int id);
	List<Categories> findAllByOrderBySortingAsc();
}
