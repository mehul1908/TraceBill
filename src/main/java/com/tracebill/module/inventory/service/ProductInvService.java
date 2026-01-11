package com.tracebill.module.inventory.service;

import java.math.BigInteger;

public interface ProductInvService {


	void addQuantity(Long prodInvId, BigInteger manufacturedQty);

	Long getProdInvByProdAndPartyOrCreate(Long productId, Long partyId);

}
