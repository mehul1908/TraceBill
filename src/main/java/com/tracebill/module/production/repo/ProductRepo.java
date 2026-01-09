package com.tracebill.module.production.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.production.entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

}
