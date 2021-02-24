package com.swati.shopping.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swati.shopping.security.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByUsername(String username);

}
