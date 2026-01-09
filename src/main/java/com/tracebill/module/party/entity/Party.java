package com.tracebill.module.party.entity;

import java.util.List;

import com.tracebill.module.party.enums.PartyType;
import com.tracebill.module.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "party" ,
	indexes = {
    		@Index(name = "idx_users_email", columnList = "email"),
    		@Index(name = "idx_party_parent", columnList = "parentPartyId")

    }
		)
public class Party {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long partyId;

	@Column(unique=true , nullable=false)
	private String email;
	
	private Long parentPartyId;
	
	@Builder.Default
	private boolean active = true;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyType type;
	
	
}
