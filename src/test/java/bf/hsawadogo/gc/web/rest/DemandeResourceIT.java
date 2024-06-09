package bf.hsawadogo.gc.web.rest;

import static bf.hsawadogo.gc.domain.DemandeAsserts.*;
import static bf.hsawadogo.gc.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bf.hsawadogo.gc.IntegrationTest;
import bf.hsawadogo.gc.domain.Demande;
import bf.hsawadogo.gc.domain.enumeration.Etat;
import bf.hsawadogo.gc.repository.DemandeRepository;
import bf.hsawadogo.gc.service.dto.DemandeDTO;
import bf.hsawadogo.gc.service.mapper.DemandeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DemandeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DemandeResourceIT {

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());

    private static final Etat DEFAULT_ETAT = Etat.EN_ATTENTE;
    private static final Etat UPDATED_ETAT = Etat.VALIDEE;

    private static final String ENTITY_API_URL = "/api/demandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandeMapper demandeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDemandeMockMvc;

    private Demande demande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createEntity(EntityManager em) {
        Demande demande = new Demande().motif(DEFAULT_MOTIF).dateDebut(DEFAULT_DATE_DEBUT).dateFin(DEFAULT_DATE_FIN).etat(DEFAULT_ETAT);
        return demande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createUpdatedEntity(EntityManager em) {
        Demande demande = new Demande().motif(UPDATED_MOTIF).dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN).etat(UPDATED_ETAT);
        return demande;
    }

    @BeforeEach
    public void initTest() {
        demande = createEntity(em);
    }

    @Test
    @Transactional
    void createDemande() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);
        var returnedDemandeDTO = om.readValue(
            restDemandeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DemandeDTO.class
        );

        // Validate the Demande in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDemande = demandeMapper.toEntity(returnedDemandeDTO);
        assertDemandeUpdatableFieldsEquals(returnedDemande, getPersistedDemande(returnedDemande));
    }

    @Test
    @Transactional
    void createDemandeWithExistingId() throws Exception {
        // Create the Demande with an existing ID
        demande.setId(1L);
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDemandes() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demande.getId().intValue())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT.toString())));
    }

    @Test
    @Transactional
    void getDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get the demande
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL_ID, demande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(demande.getId().intValue()))
            .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()))
            .andExpect(jsonPath("$.etat").value(DEFAULT_ETAT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDemande() throws Exception {
        // Get the demande
        restDemandeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demande
        Demande updatedDemande = demandeRepository.findById(demande.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDemande are not directly saved in db
        em.detach(updatedDemande);
        updatedDemande.motif(UPDATED_MOTIF).dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN).etat(UPDATED_ETAT);
        DemandeDTO demandeDTO = demandeMapper.toDto(updatedDemande);

        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDemandeToMatchAllProperties(updatedDemande);
    }

    @Test
    @Transactional
    void putNonExistingDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        partialUpdatedDemande.motif(UPDATED_MOTIF).etat(UPDATED_ETAT);

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDemande, demande), getPersistedDemande(demande));
    }

    @Test
    @Transactional
    void fullUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        partialUpdatedDemande.motif(UPDATED_MOTIF).dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN).etat(UPDATED_ETAT);

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeUpdatableFieldsEquals(partialUpdatedDemande, getPersistedDemande(partialUpdatedDemande));
    }

    @Test
    @Transactional
    void patchNonExistingDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the demande
        restDemandeMockMvc
            .perform(delete(ENTITY_API_URL_ID, demande.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return demandeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Demande getPersistedDemande(Demande demande) {
        return demandeRepository.findById(demande.getId()).orElseThrow();
    }

    protected void assertPersistedDemandeToMatchAllProperties(Demande expectedDemande) {
        assertDemandeAllPropertiesEquals(expectedDemande, getPersistedDemande(expectedDemande));
    }

    protected void assertPersistedDemandeToMatchUpdatableProperties(Demande expectedDemande) {
        assertDemandeAllUpdatablePropertiesEquals(expectedDemande, getPersistedDemande(expectedDemande));
    }
}
