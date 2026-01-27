package com.tracebill.module.production.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.production.dto.ProductRegisterModel;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.repo.ProductRepo;
import com.tracebill.module.user.enums.UserRole;
import com.tracebill.util.HashService;
import com.tracebill.util.SequenceGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private SequenceGeneratorService sequenceGenerator;
	
	@Autowired
	private AuthenticatedUserProvider authenticatedUser;
	
	@Autowired
	private HashService hashService;
	
	@Autowired
	private AuditLogService auditService;
	
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public Long createProduct(ProductRegisterModel model) {

		
		UserRole userRole = authenticatedUser.getAuthenticatedUserRole();
		
		if(userRole!=UserRole.ROLE_ADMIN) {
			throw new UnauthorizedUserException("Only Admin is allowed to create product");
		}
		
		long seq = sequenceGenerator.nextProductSeq();
        String prodCode = String.format("P%03d", seq);
        String prodHash = hashService.generateProductHash(prodCode, model.getProdName(), model.getMrp());
        
		Product product = Product.builder()
				.prodName(model.getProdName())
				.prodCode(prodCode)
				.mrp(model.getMrp())
				.defaultRate(model.getDefaultRate())
				.gstRate(model.getGstRate())
				.cessRate(model.getCessRate())
				.productHash(prodHash)
				.build();
		
		Product saved =productRepo.save(product);
				
		return saved.getProductId();
	}

	@Override
	public boolean existById(Long productId) {
		return productRepo.existsById(productId);
	}

	@Override
	public Product getProductById(Long productId) {
		return productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + productId));
	}

}
