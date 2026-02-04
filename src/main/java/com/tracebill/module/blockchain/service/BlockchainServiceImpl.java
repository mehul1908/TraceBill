package com.tracebill.module.blockchain.service;

import java.math.BigInteger;

import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import com.tracebill.module.blockchain.contract.SupplyChainContract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainServiceImpl implements BlockchainService {

    private final SupplyChainContract contract;

    @Override
    public TransactionReceipt transferBatch(
            String batchHash,
            String toWallet,
            int quantity
    ) {
    	log.info("🚀 transferBatch batchHash={} qty={}",
    	         batchHash, quantity);
        try {
             return 
                    contract.transferBatch(
                            Numeric.hexStringToByteArray(batchHash),
                            toWallet,
                            BigInteger.valueOf(quantity)
                    ).send();

            

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Blockchain batch transfer failed for batchHash=" + batchHash,
                    e
            );
        }
    }
    
    
    @Override
    public TransactionReceipt productCreation( String prodHash) {
	    	try {
	    		byte[] productHashBytes = Numeric.hexStringToByteArray(prodHash);
	    		return contract.registerProduct(productHashBytes).send();
	    	}catch (Exception e) {
	            throw new IllegalStateException(
	                    "Blockchain product registration failed for batchHash=" + prodHash,
	                    e
	            );
	        }
    }
}
