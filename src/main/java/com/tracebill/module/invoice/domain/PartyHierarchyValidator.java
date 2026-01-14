package com.tracebill.module.invoice.domain;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.enums.PartyType;

@Component
public class PartyHierarchyValidator {

    public void validate(Party seller, Party buyer) {

        boolean validChain =
                (seller.getType() == PartyType.MANUFACTURER && buyer.getType() == PartyType.WAREHOUSE)
             || (seller.getType() == PartyType.WAREHOUSE && buyer.getType() == PartyType.WHOLESALER)
             || (seller.getType() == PartyType.WHOLESALER && buyer.getType() == PartyType.RETAILER);

        boolean validParent =
                Objects.equals(buyer.getParentPartyId(), seller.getPartyId());

        if (!validChain || !validParent) {
            throw new IllegalStateException("Invalid party hierarchy for invoice");
        }
    }
}

