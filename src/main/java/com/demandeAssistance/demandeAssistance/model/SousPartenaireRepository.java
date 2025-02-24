package com.demandeAssistance.demandeAssistance.model;

import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SousPartenaireRepository extends JpaRepository<SousPartenaire, Long> {
}
