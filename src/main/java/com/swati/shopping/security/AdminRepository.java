package com.swati.shopping.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swati.shopping.security.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
	
	Admin findByUsername(String username);

}
