package com.tracebill.module.party.enums;

public enum PartyType {
	MANUFACTURER("MFG"),
    WAREHOUSE("WH"),
    WHOLESALER("WS"),
    RETAILER("RET"),
    TRANSPORTER("TR");
	
	private final String prefix;

    PartyType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}

