package com.tracebill.module.blockchain.dto;

import java.math.BigInteger;

import lombok.Data;
import org.web3j.protocol.core.methods.response.Log;

@Data
public class BatchTransferredEvent {

    private String txHash;
    private String batchHash;
    private String from;
    private String to;
    private BigInteger quantity;
    private BigInteger timestamp;
    private Log rawLog;
}
