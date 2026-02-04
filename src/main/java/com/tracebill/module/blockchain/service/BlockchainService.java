package com.tracebill.module.blockchain.service;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public interface BlockchainService {

    TransactionReceipt transferBatch(
            String batchHash,
            String toWallet,
            int quantity
    );

	TransactionReceipt productCreation(String prodHash);
}

