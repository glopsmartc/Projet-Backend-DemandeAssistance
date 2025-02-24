package com.demandeAssistance.demandeAssistance.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DossierAssistanceRepository extends CrudRepository<DossierAssistance, Long> {
    List<DossierAssistance> findByIdContrat(Long idContrat);
    Optional<DossierAssistance> findById(Long id);
}


