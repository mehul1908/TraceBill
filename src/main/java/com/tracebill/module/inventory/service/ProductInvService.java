package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import com.tracebill.module.inventory.entity.ProductInventory;

public interface ProductInvService {


	void addQuantity(Long prodInvId, BigInteger manufacturedQty);

	Long getProdInvByProdAndPartyOrCreate(Long productId, Long partyId);

	ProductInventory getProdInvByProdAndParty(Long productId, Long ownerId);

	void save(ProductInventory productInv);

}
