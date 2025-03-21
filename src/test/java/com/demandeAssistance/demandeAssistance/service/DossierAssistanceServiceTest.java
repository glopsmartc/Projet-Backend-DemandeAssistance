package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.config.ContratClientService;
import com.demandeAssistance.demandeAssistance.config.UserClientService;
import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.dto.UtilisateurDTO;
import com.demandeAssistance.demandeAssistance.exception.DossierAssistanceNotFoundException;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.DossierAssistanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DossierAssistanceServiceTest {

    @Mock
    private DossierAssistanceRepository dossierAssistanceRepository;

    @Mock
    private UserClientService userClientService;

    @Mock
    private ContratClientService contratClientService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DossierAssistanceService dossierAssistanceService;

    private CreationDossierDTO creationDossierDTO;
    private DossierAssistance dossierAssistance;
    private UtilisateurDTO utilisateurDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dossierAssistanceService = new DossierAssistanceService(
                dossierAssistanceRepository, userClientService, contratClientService, notificationService);
        try {
            java.lang.reflect.Field storagePathField = DossierAssistanceService.class.getDeclaredField("storagePath");
            storagePathField.setAccessible(true);
            storagePathField.set(dossierAssistanceService, "target/test-files");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la configuration de storagePath", e);
        }

        creationDossierDTO = new CreationDossierDTO();
        creationDossierDTO.setDescription("Test Description");
        creationDossierDTO.setType("Assistance Médicale");
        creationDossierDTO.setIdContrat(1L);
        creationDossierDTO.setNumTel("0123456789");
        creationDossierDTO.setEmail("test@example.com");
        creationDossierDTO.setMaladieChronique(true);
        creationDossierDTO.setDescriptionMaladie("Diabète");
        creationDossierDTO.setPositionActuelle("Paris");
        creationDossierDTO.setPriorite("Haute");

        dossierAssistance = new DossierAssistance();
        dossierAssistance.setIdDossier(1L);
        dossierAssistance.setDescription("Test Description");
        dossierAssistance.setTypeAssistance("Assistance Médicale");
        dossierAssistance.setIdContrat(1L);
        dossierAssistance.setNumTel("0123456789");
        dossierAssistance.setEmail("test@example.com");
        dossierAssistance.setDateOuverture(LocalDate.now());
        dossierAssistance.setStatutDossier("En attente");
        dossierAssistance.setPositionActuelle("Paris");
        dossierAssistance.setPriorite("Haute");
        dossierAssistance.setFraisTotalDepense(0.0);

        utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setEmail("test@example.com");
        utilisateurDTO.setNom("Doe");
        utilisateurDTO.setPrenom("John");
        utilisateurDTO.setRole("CLIENT");
    }



    @Test
    void createDossier_NoMaladieChronique_ShouldNotUpdateUser() throws Exception {
        creationDossierDTO.setMaladieChronique(false);
        when(dossierAssistanceRepository.save(any(DossierAssistance.class))).thenReturn(dossierAssistance);

        DossierAssistance result = dossierAssistanceService.createDossier(creationDossierDTO, null, "token");

        assertNotNull(result);
        verify(userClientService, never()).updateMaladieChronique(anyBoolean(), anyString(), anyString());
        verify(notificationService).notifyLogisticien("Un nouveau dossier d'assistance a été créé : Test Description");
    }

    @Test
    void createDossier_InvalidFile_ShouldThrowException() throws Exception {
        List<MultipartFile> documents = new ArrayList<>();
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("");
        documents.add(mockFile);

        when(userClientService.getAuthenticatedUser(anyString())).thenReturn(utilisateurDTO);

        assertThrows(IllegalArgumentException.class, () ->
                dossierAssistanceService.createDossier(creationDossierDTO, documents, "token"));
    }


    @Test
    void savePdfFile_ShouldSaveFileAndReturnPath() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(userClientService.getAuthenticatedUser("token")).thenReturn(utilisateurDTO);
        doNothing().when(mockFile).transferTo(any(File.class));

        java.lang.reflect.Method savePdfFileMethod = DossierAssistanceService.class.getDeclaredMethod(
                "savePdfFile", MultipartFile.class, Long.class, String.class, String.class);
        savePdfFileMethod.setAccessible(true);
        String filePath = (String) savePdfFileMethod.invoke(dossierAssistanceService, mockFile, 1L, "documents", "token");

        assertTrue(filePath.contains("Doe_John_1_test.pdf"));
    }

    @Test
    void savePdfFile_NullFile_ShouldThrowException() {
        try {
            java.lang.reflect.Method savePdfFileMethod = DossierAssistanceService.class.getDeclaredMethod(
                    "savePdfFile", MultipartFile.class, Long.class, String.class, String.class);
            savePdfFileMethod.setAccessible(true);
            savePdfFileMethod.invoke(dossierAssistanceService, null, 1L, "documents", "token");
            fail("Une exception aurait dû être levée");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    void savePdfFile_EmptyFilename_ShouldThrowException() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("");

        try {
            java.lang.reflect.Method savePdfFileMethod = DossierAssistanceService.class.getDeclaredMethod(
                    "savePdfFile", MultipartFile.class, Long.class, String.class, String.class);
            savePdfFileMethod.setAccessible(true);
            savePdfFileMethod.invoke(dossierAssistanceService, mockFile, 1L, "documents", "token");
            fail("Une exception aurait dû être levée");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    void savePdfFile_DirectoryCreation_ShouldCreateDirectory() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(userClientService.getAuthenticatedUser("token")).thenReturn(utilisateurDTO);
        doNothing().when(mockFile).transferTo(any(File.class));

        Path directory = Paths.get("target/test-files", "documents").toAbsolutePath().normalize();
        File dir = directory.toFile();
        if (dir.exists()) {
            dir.delete();
        }

        java.lang.reflect.Method savePdfFileMethod = DossierAssistanceService.class.getDeclaredMethod(
                "savePdfFile", MultipartFile.class, Long.class, String.class, String.class);
        savePdfFileMethod.setAccessible(true);
        String filePath = (String) savePdfFileMethod.invoke(dossierAssistanceService, mockFile, 1L, "documents", "token");

        assertTrue(filePath.contains("Doe_John_1_test.pdf"));
    }




    @Test
    void getContratDossiers_ShouldReturnDossiers() {
        List<DossierAssistance> dossiers = Collections.singletonList(dossierAssistance);
        when(dossierAssistanceRepository.findByIdContrat(1L)).thenReturn(dossiers);

        List<DossierAssistance> result = dossierAssistanceService.getContratDossiers(1L);

        assertEquals(1, result.size());
        assertEquals(dossierAssistance, result.get(0));
    }


    @Test
    void getAllDossiers_ShouldReturnAllDossiers() {
        List<DossierAssistance> dossiers = Collections.singletonList(dossierAssistance);
        when(dossierAssistanceRepository.findAll()).thenReturn(dossiers);

        List<DossierAssistance> result = dossierAssistanceService.getAllDossiers();

        assertEquals(1, result.size());
        assertEquals(dossierAssistance, result.get(0));
    }


    @Test
    void getOffreDesciptionByContratId_ShouldReturnDescription() {
        when(contratClientService.getContratById(1L, "token")).thenReturn("Description");

        String result = dossierAssistanceService.getOffreDesciptionByContratId(1L, "token");

        assertEquals("Description", result);
    }

    @Test
    void getOffreDesciptionByContratId_NullDescription_ShouldReturnDefault() {
        when(contratClientService.getContratById(1L, "token")).thenReturn(null);

        String result = dossierAssistanceService.getOffreDesciptionByContratId(1L, "token");

        assertEquals("Aucune description disponible", result);
    }

    @Test
    void getOffreDesciptionByContratId_EmptyDescription_ShouldReturnDefault() {
        when(contratClientService.getContratById(1L, "token")).thenReturn("");

        String result = dossierAssistanceService.getOffreDesciptionByContratId(1L, "token");

        assertEquals("Aucune description disponible", result);
    }

    @Test
    void getOffreDesciptionByContratId_Exception_ShouldReturnErrorMessage() {
        when(contratClientService.getContratById(1L, "token")).thenThrow(new RuntimeException("Erreur"));

        String result = dossierAssistanceService.getOffreDesciptionByContratId(1L, "token");

        assertEquals("Erreur lors de la récupération de la description du contrat", result);
    }


    @Test
    void getAllDossiersClient_WithContrats_ShouldReturnDossiers() {
        List<Long> contratIds = Collections.singletonList(1L);
        List<DossierAssistance> dossiers = Collections.singletonList(dossierAssistance);
        when(contratClientService.getContratsIdForClient("token")).thenReturn(contratIds);
        when(dossierAssistanceRepository.findByIdContrat(1L)).thenReturn(dossiers);

        List<DossierAssistance> result = dossierAssistanceService.getAllDossiersClient("token");

        assertEquals(1, result.size());
        assertEquals(dossierAssistance, result.get(0));
    }

    @Test
    void getAllDossiersClient_NoContrats_ShouldReturnEmptyList() {
        when(contratClientService.getContratsIdForClient("token")).thenReturn(Collections.emptyList());

        List<DossierAssistance> result = dossierAssistanceService.getAllDossiersClient("token");

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllDossiersClient_WithException_ShouldReturnPartialList() {
        List<Long> contratIds = List.of(1L, 2L);
        List<DossierAssistance> dossiers = Collections.singletonList(dossierAssistance);
        when(contratClientService.getContratsIdForClient("token")).thenReturn(contratIds);
        when(dossierAssistanceRepository.findByIdContrat(1L)).thenReturn(dossiers);
        when(dossierAssistanceRepository.findByIdContrat(2L)).thenThrow(new RuntimeException("Erreur"));

        List<DossierAssistance> result = dossierAssistanceService.getAllDossiersClient("token");

        assertEquals(1, result.size());
        assertEquals(dossierAssistance, result.get(0));
    }

    @Test
    void getAllDossiersClient_NullDossiers_ShouldReturnEmptyList() {
        List<Long> contratIds = Collections.singletonList(1L);
        when(contratClientService.getContratsIdForClient("token")).thenReturn(contratIds);
        when(dossierAssistanceRepository.findByIdContrat(1L)).thenReturn(null);

        List<DossierAssistance> result = dossierAssistanceService.getAllDossiersClient("token");

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllDossiersClient_EmptyDossiers_ShouldReturnEmptyList() {
        List<Long> contratIds = Collections.singletonList(1L);
        when(contratClientService.getContratsIdForClient("token")).thenReturn(contratIds);
        when(dossierAssistanceRepository.findByIdContrat(1L)).thenReturn(Collections.emptyList());

        List<DossierAssistance> result = dossierAssistanceService.getAllDossiersClient("token");

        assertTrue(result.isEmpty());
    }


    @Test
    void getDossierById_ShouldReturnDossier() {
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.of(dossierAssistance));

        Optional<DossierAssistance> result = dossierAssistanceService.getDossierById(1L);

        assertTrue(result.isPresent());
        assertEquals(dossierAssistance, result.get());
    }


    @Test
    void updateStatutDossier_ShouldUpdateDossier() {
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.of(dossierAssistance));
        when(dossierAssistanceRepository.save(any(DossierAssistance.class))).thenReturn(dossierAssistance);

        DossierAssistance result = dossierAssistanceService.updateStatutDossier(1L, "Traité");

        assertEquals("Traité", result.getStatutDossier());
    }

    @Test
    void updateStatutDossier_DossierNotFound_ShouldThrowException() {
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                dossierAssistanceService.updateStatutDossier(1L, "Traité"));
    }


    @Test
    void assignerPartenaireDossier_ShouldAssignPartner() {
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.of(dossierAssistance));
        when(dossierAssistanceRepository.save(any(DossierAssistance.class))).thenReturn(dossierAssistance);

        DossierAssistance result = dossierAssistanceService.assignerPartenaireDossier(2L, 1L);

        assertEquals(2L, result.getPartenaire());
    }

    @Test
    void assignerPartenaireDossier_DossierNotFound_ShouldThrowException() {
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DossierAssistanceNotFoundException.class, () ->
                dossierAssistanceService.assignerPartenaireDossier(2L, 1L));
    }
}