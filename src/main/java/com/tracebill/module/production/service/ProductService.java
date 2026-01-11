package com.tracebill.module.production.service;

import com.tracebill.module.production.dto.ProductRegisterModel;

import jakarta.validation.Valid;

public interface ProductService {

	Long createProduct(ProductRegisterModel model);

	boolean existById(Long productId);

}
