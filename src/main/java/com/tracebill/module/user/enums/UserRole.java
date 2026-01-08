package com.tracebill.module.user.enums;


public enum UserRole {

    ROLE_WHOLESALER("WHOLESALER"),
    ROLE_WAREHOUSE("WAREHOUSE"),
    ROLE_TRANSPORTER("TRANSPORTER"),
    ROLE_MANUFACTURER("MANUFACTURER"),
    ROLE_ADMIN("ADMIN");

    private String name;
    
    UserRole(String name) {
        this.name = name;
    }
 
    public String displayName() {
    	return this.name;
    }
}

