package com.tracebill.config;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import com.tracebill.module.blockchain.contract.SupplyChainContract;

//Blockchain Configuration
@Configuration
public class BlockchainConfig {

    @Value("${blockchain.rpc-url}")
    private String rpcUrl;

    @Value("${blockchain.private-key}")
    private String privateKey;

    @Value("${blockchain.contract-address}")
    private String contractAddress;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpcUrl));
    }

    @Bean
    public Credentials credentials() {
        Credentials credentials = Credentials.create(privateKey);

        System.out.println("Web3j wallet address: " + credentials.getAddress());

        return credentials;
    }


    @Bean
    public ContractGasProvider gasProvider() {
        return new StaticGasProvider(
                BigInteger.valueOf(20_000_000_000L), // gas price (20 Gwei)
                BigInteger.valueOf(3_000_000)        // gas limit
        );
    }

    @Bean
    public SupplyChainContract supplyChainContract(
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider gasProvider
    ) {
        return SupplyChainContract.load(
                contractAddress,
                web3j,
                credentials,
                gasProvider
        );
    }
}
