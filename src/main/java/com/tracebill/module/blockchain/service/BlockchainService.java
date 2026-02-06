package com.tracebill.module.blockchain.service;

import java.math.BigInteger;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public interface BlockchainService {

    TransactionReceipt transferBatch(
            String batchHash,
            String toWallet,
            int quantity
    );

	TransactionReceipt productCreation(String prodHash);

	TransactionReceipt batchCreation(String batchHash, String prodHash, BigInteger qty);

	TransactionReceipt invoiceAnchor(String invoiceHash, String to);

}

