package com.tracebill.module.party.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.enums.PartyType;

@Repository
public interface PartyRepo extends JpaRepository<Party, Long> {

	Optional<Party> findByEmail(String email);

	Optional<Party> findByEmailAndNotType(String email, PartyType type);

}
