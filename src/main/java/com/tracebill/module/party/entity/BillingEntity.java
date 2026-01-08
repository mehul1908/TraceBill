package com.tracebill.module.party.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "billing_entities",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "gst_no")
    },
    indexes = {
    		@Index(name = "idx_users_email", columnList = "emailId")
    }
)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BillingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingEntityId;

    @Pattern(
		    regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
		    message = "Invalid GST number format"
		)
    @Column(name = "gst_no", unique = true , length = 15)
    private String gstNo;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "emailId" , nullable = false)
    private String emailId;
    
    private Boolean registered; 
}

