package com.tracebill.module.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tracebill.module.party.entity.Party;
import com.tracebill.module.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "users",
indexes = {
        @Index(name = "idx_users_email", columnList = "email")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Email
    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Builder.Default
    private boolean active = true;

    @Pattern(
        regexp = "^0x[a-fA-F0-9]{40}$",
        message = "Invalid Ethereum wallet address"
    )
    @Column(nullable = false, unique = true, length = 42)
    @JsonIgnore
    private String walletAddress;
    
    @Column(unique = true)
    private Long partyId;

    
}

