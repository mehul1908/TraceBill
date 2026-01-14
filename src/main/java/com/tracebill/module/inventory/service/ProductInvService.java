package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import com.tracebill.module.inventory.entity.ProductInventory;

public interface ProductInvService {

    ProductInventory getProdInvByProdAndParty(
            Long productId,
            Long partyId
    );

    void addQuantity(
            Long prodInvId,
            BigInteger qty
    );

    void subtractQuantity(
            Long productId,
            BigInteger qty
    );

    void save(ProductInventory inventory);

	Long getProdInvByProdAndPartyOrCreate(Long productId, Long partyId);
}

