package com.demandeAssistance.demandeAssistance.model;

import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SousPartenaireRepository extends JpaRepository<SousPartenaire, Long> {
    Optional<SousPartenaire> findByNumTel(String numTel);
}
