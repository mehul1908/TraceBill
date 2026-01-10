package com.tracebill.module.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.auth.entity.SecurityUser;
import com.tracebill.module.user.entity.User;
import com.tracebill.module.user.enums.UserRole;

@Component
public class AuthenticatedUserProvider {

    public Long getAuthenticatedParty() {
    	System.out.println("In get Authenticated User");
    	Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
            || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedUserException("User is unauthenticated");
        }
        
        SecurityUser secUser = (SecurityUser) auth.getPrincipal();
        return secUser.getUser().getPartyId();
    }
    
    public UserRole getAuthenticatedUserRole() {
    	System.out.println("In get Authenticated User");
    	Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
            || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedUserException("User is unauthenticated");
        }
        
        SecurityUser secUser = (SecurityUser) auth.getPrincipal();
        return secUser.getUser().getRole();
    }
}

