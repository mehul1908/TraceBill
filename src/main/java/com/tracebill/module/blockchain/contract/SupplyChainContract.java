package com.tracebill.module.blockchain.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;

import io.reactivex.Flowable;

/**
 * Web3j contract wrapper for SupplyChain.sol
 * Acts as a thin adapter over the blockchain.
 */
public class SupplyChainContract extends Contract {

    private static final String BINARY = "";

    private static final String ABI = """
    [
	{
		"anonymous": false,
		"inputs": [
			{
				"indexed": true,
				"internalType": "bytes32",
				"name": "batchHash",
				"type": "bytes32"
			},
			{
				"indexed": true,
				"internalType": "bytes32",
				"name": "productHash",
				"type": "bytes32"
			},
			{
				"indexed": true,
				"internalType": "address",
				"name": "owner",
				"type": "address"
			},
			{
				"indexed": false,
				"internalType": "uint256",
				"name": "quantity",
				"type": "uint256"
			},
			{
				"indexed": false,
				"internalType": "uint256",
				"name": "timestamp",
				"type": "uint256"
			}
		],
		"name": "BatchRegistered",
		"type": "event"
	},
	{
		"anonymous": false,
		"inputs": [
			{
				"indexed": true,
				"internalType": "bytes32",
				"name": "batchHash",
				"type": "bytes32"
			},
			{
				"indexed": true,
				"internalType": "address",
				"name": "from",
				"type": "address"
			},
			{
				"indexed": true,
				"internalType": "address",
				"name": "to",
				"type": "address"
			},
			{
				"indexed": false,
				"internalType": "uint256",
				"name": "quantity",
				"type": "uint256"
			},
			{
				"indexed": false,
				"internalType": "uint256",
				"name": "timestamp",
				"type": "uint256"
			}
		],
		"name": "BatchTransferred",
		"type": "event"
	},
	{
		"anonymous": false,
		"inputs": [
			{
				"indexed": true,
				"internalType": "bytes32",
				"name": "invoiceHash",
				"type": "bytes32"
			},
			{
				"indexed": true,
				"internalType": "address",
				"name": "from",
				"type": "address"
			},
			{
				"indexed": true,
				"internalType": "address",
				"name": "to",
				"type": "address"
			},
			{
				"indexed": false,
				"internalType": "uint256",
				"name": "timestamp",
				"type": "uint256"
			}
		],
		"name": "InvoiceAnchored",
		"type": "event"
	},
	{
		"anonymous": false,
		"inputs": [
			{
				"indexed": true,
				"internalType": "bytes32",
				"name": "productHash",
				"type": "bytes32"
			},
			{
				"indexed": true,
				"internalType": "address",
				"name": "manufacturer",
				"type": "address"
			},
			{
				"indexed": false,
				"internalType": "uint256",
				"name": "timestamp",
				"type": "uint256"
			}
		],
		"name": "ProductRegistered",
		"type": "event"
	},
	{
		"inputs": [
			{
				"internalType": "bytes32",
				"name": "invoiceHash",
				"type": "bytes32"
			},
			{
				"internalType": "address",
				"name": "to",
				"type": "address"
			}
		],
		"name": "anchorInvoice",
		"outputs": [],
		"stateMutability": "nonpayable",
		"type": "function"
	},
	{
		"inputs": [
			{
				"internalType": "bytes32",
				"name": "",
				"type": "bytes32"
			}
		],
		"name": "batches",
		"outputs": [
			{
				"internalType": "bool",
				"name": "exists",
				"type": "bool"
			},
			{
				"internalType": "bytes32",
				"name": "productHash",
				"type": "bytes32"
			},
			{
				"internalType": "address",
				"name": "owner",
				"type": "address"
			},
			{
				"internalType": "uint256",
				"name": "quantity",
				"type": "uint256"
			},
			{
				"internalType": "uint256",
				"name": "registeredAt",
				"type": "uint256"
			}
		],
		"stateMutability": "view",
		"type": "function"
	},
	{
		"inputs": [
			{
				"internalType": "bytes32",
				"name": "",
				"type": "bytes32"
			}
		],
		"name": "products",
		"outputs": [
			{
				"internalType": "bool",
				"name": "exists",
				"type": "bool"
			},
			{
				"internalType": "address",
				"name": "manufacturer",
				"type": "address"
			},
			{
				"internalType": "uint256",
				"name": "registeredAt",
				"type": "uint256"
			}
		],
		"stateMutability": "view",
		"type": "function"
	},
	{
		"inputs": [
			{
				"internalType": "bytes32",
				"name": "batchHash",
				"type": "bytes32"
			},
			{
				"internalType": "bytes32",
				"name": "productHash",
				"type": "bytes32"
			},
			{
				"internalType": "uint256",
				"name": "quantity",
				"type": "uint256"
			}
		],
		"name": "registerBatch",
		"outputs": [],
		"stateMutability": "nonpayable",
		"type": "function"
	},
	{
		"inputs": [
			{
				"internalType": "bytes32",
				"name": "productHash",
				"type": "bytes32"
			}
		],
		"name": "registerProduct",
		"outputs": [],
		"stateMutability": "nonpayable",
		"type": "function"
	},
	{
		"inputs": [
			{
				"internalType": "bytes32",
				"name": "batchHash",
				"type": "bytes32"
			},
			{
				"internalType": "address",
				"name": "to",
				"type": "address"
			},
			{
				"internalType": "uint256",
				"name": "quantity",
				"type": "uint256"
			}
		],
		"name": "transferBatch",
		"outputs": [],
		"stateMutability": "nonpayable",
		"type": "function"
	}
]
    """;

    protected SupplyChainContract(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider gasProvider
    ) {
        super(ABI, contractAddress, web3j, credentials, gasProvider);
    }

    public static SupplyChainContract load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider gasProvider
    ) {
        return new SupplyChainContract(
                contractAddress,
                web3j,
                credentials,
                gasProvider
        );
    }

    /* ==========================================================
       CONTRACT FUNCTIONS
       ========================================================== */

    public RemoteFunctionCall<TransactionReceipt> registerProduct(
            byte[] productHash
    ) {
        Function function = new Function(
                "registerProduct",
                List.of(new Bytes32(productHash)),
                Collections.emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> registerBatch(
            byte[] batchHash,
            byte[] productHash,
            BigInteger quantity
    ) {
        Function function = new Function(
                "registerBatch",
                Arrays.asList(
                        new Bytes32(batchHash),
                        new Bytes32(productHash),
                        new Uint256(quantity)
                ),
                Collections.emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferBatch(
            byte[] batchHash,
            String to,
            BigInteger quantity
    ) {
        Function function = new Function(
                "transferBatch",
                Arrays.asList(
                        new Bytes32(batchHash),
                        new Address(to),
                        new Uint256(quantity)
                ),
                Collections.emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> anchorInvoice(
            byte[] invoiceHash,
            String to
    ) {
        Function function = new Function(
                "anchorInvoice",
                Arrays.asList(
                        new Bytes32(invoiceHash),
                        new Address(to)
                ),
                Collections.emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    /* ==========================================================
       EVENTS
       ========================================================== */

    public static final Event PRODUCTREGISTERED_EVENT = new Event(
            "ProductRegistered",
            Arrays.asList(
                    new TypeReference<Bytes32>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Uint256>() {}
            )
    );

    public static final Event BATCHREGISTERED_EVENT = new Event(
            "BatchRegistered",
            Arrays.asList(
                    new TypeReference<Bytes32>(true) {},
                    new TypeReference<Bytes32>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Uint256>() {},
                    new TypeReference<Uint256>() {}
            )
    );

    public static final Event BATCHTRANSFERRED_EVENT = new Event(
            "BatchTransferred",
            Arrays.asList(
                    new TypeReference<Bytes32>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Uint256>() {},
                    new TypeReference<Uint256>() {}
            )
    );

    public static final Event INVOICEANCHORED_EVENT = new Event(
            "InvoiceAnchored",
            Arrays.asList(
                    new TypeReference<Bytes32>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Uint256>() {}
            )
    );

    /* ==========================================================
       EVENT FLOWABLES
       ========================================================== */

    public Flowable<Log> eventFlowable(
            Event event,
            DefaultBlockParameter start,
            DefaultBlockParameter end
    ) {
        EthFilter filter = new EthFilter(start, end, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogFlowable(filter);
    }

    public static EventValues extractEventValues(
            Event event,
            Log log
    ) {
        return staticExtractEventParameters(event, log);
    }
}
