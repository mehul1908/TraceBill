package com.tracebill.module.inventory.service;

import java.math.BigInteger;

public interface BatchInvService {


	Long createBatchInventory(Long batchId, Long prodInvId, BigInteger manufacturedQty);

}
