package com.tracebill.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
public class HashService {

    public String generateProductHash(String productCode, String name, BigDecimal mrp) {
        String canonical = String.join("|", productCode, name.trim(), mrp.setScale(2, RoundingMode.HALF_UP).toString());
        return sha256(canonical);
    }

    public String generateBatchHash(String batchNo, String productHash, Long factoryId, LocalDate manufactureDate , Long version) {
        String canonical = String.join("|", batchNo, productHash, factoryId.toString(), manufactureDate.toString(), version.toString());
        return sha256(canonical);
    }

    private String sha256(String input) {
    	try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return "0x" + hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
