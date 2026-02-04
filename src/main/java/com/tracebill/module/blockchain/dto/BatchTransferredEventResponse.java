package com.tracebill.module.blockchain.dto;

import java.math.BigInteger;
import org.web3j.protocol.core.methods.response.Log;

public class BatchTransferredEventResponse {
    public String batchHash;
    public String from;
    public String to;
    public BigInteger quantity;
    public BigInteger timestamp;
    public Log log;
}

