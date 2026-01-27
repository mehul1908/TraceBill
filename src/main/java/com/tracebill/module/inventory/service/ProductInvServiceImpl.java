package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.entity.ProductInventory;
import com.tracebill.module.inventory.repo.ProductInvRepo;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductInvServiceImpl implements ProductInvService {

    private final AuthenticatedUserProvider authenticatedUser;
    private final ProductInvRepo productInvRepo;
    
    @PostConstruct
    public void init() {
        System.out.println("LOADED: ProductInvServiceImpl");
    }


    @Override
    public ProductInventory getProdInvByProdAndParty(
            Long productId,
            Long partyId
    ) {
        return productInvRepo
                .findByProductIdAndOwnerId(productId, partyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product inventory not found"
                        )
                );
    }

    @Override
    @Transactional
    public void addQuantity(Long prodInvId, BigInteger qty) {

        ProductInventory inv =
                productInvRepo.findById(prodInvId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product inventory not found"
                                )
                        );

        inv.addStock(qty);
        productInvRepo.save(inv);
    }

    @Override
    @Transactional
    public void subtractQuantity(Long productId, BigInteger qty) {

        Long ownerId = authenticatedUser.getAuthenticatedParty();

        ProductInventory inv =
                getProdInvByProdAndParty(productId, ownerId);

        inv.subtractStock(qty);
        productInvRepo.save(inv);
    }

    @Override
    public void save(ProductInventory inventory) {
        productInvRepo.save(inventory);
    }
    
    @Override
	public Long getProdInvByProdAndPartyOrCreate(Long productId , Long partyId) {
    		System.out.println("In get product inv ");
		Optional<ProductInventory> prodInvOp = productInvRepo.findByProductIdAndOwnerId(productId , partyId);
		if(prodInvOp.isPresent())
			return prodInvOp.get().getProductInvId();
		
		
		ProductInventory prodInv = ProductInventory.builder()
				.productId(productId)
				.qty(BigInteger.valueOf(0))
				.ownerId(partyId)
				.build();
		
		ProductInventory saved = productInvRepo.save(prodInv);
		System.out.println(saved);
		
		return saved.getProductInvId();
		
	}
}
