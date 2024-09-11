package com.abc.restaurant.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.restaurant.models.Product;

                                              // Model name  // Type of the primary key
public interface ProductsRepository extends JpaRepository<Product, Integer> {
    
}
