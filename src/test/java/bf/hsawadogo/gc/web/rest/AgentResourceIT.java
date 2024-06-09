package bf.hsawadogo.gc.web.rest;

import static bf.hsawadogo.gc.domain.AgentAsserts.*;
import static bf.hsawadogo.gc.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bf.hsawadogo.gc.IntegrationTest;
import bf.hsawadogo.gc.domain.Agent;
import bf.hsawadogo.gc.repository.AgentRepository;
import bf.hsawadogo.gc.service.dto.AgentDTO;
import bf.hsawadogo.gc.service.mapper.AgentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link AgentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AgentResourceIT {

    private static final String DEFAULT_MATRICULE = "AAAAAAAAAA";
    private static final String UPDATED_MATRICULE = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_FONCTION = "AAAAAAAAAA";
    private static final String UPDATED_FONCTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/agents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgentMockMvc;

    private Agent agent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agent createEntity(EntityManager em) {
        Agent agent = new Agent()
            .matricule(DEFAULT_MATRICULE)
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .telephone(DEFAULT_TELEPHONE)
            .fonction(DEFAULT_FONCTION);
        return agent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agent createUpdatedEntity(EntityManager em) {
        Agent agent = new Agent()
            .matricule(UPDATED_MATRICULE)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .telephone(UPDATED_TELEPHONE)
            .fonction(UPDATED_FONCTION);
        return agent;
    }

    @BeforeEach
    public void initTest() {
        agent = createEntity(em);
    }

    @Test
    @Transactional
    void createAgent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);
        var returnedAgentDTO = om.readValue(
            restAgentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AgentDTO.class
        );

        // Validate the Agent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAgent = agentMapper.toEntity(returnedAgentDTO);
        assertAgentUpdatableFieldsEquals(returnedAgent, getPersistedAgent(returnedAgent));
    }

    @Test
    @Transactional
    void createAgentWithExistingId() throws Exception {
        // Create the Agent with an existing ID
        agent.setId(1L);
        AgentDTO agentDTO = agentMapper.toDto(agent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMatriculeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agent.setMatricule(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agent.setNom(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agent.setPrenom(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAgents() throws Exception {
        // Initialize the database
        agentRepository.saveAndFlush(agent);

        // Get all the agentList
        restAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agent.getId().intValue())))
            .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].fonction").value(hasItem(DEFAULT_FONCTION)));
    }

    @Test
    @Transactional
    void getAgent() throws Exception {
        // Initialize the database
        agentRepository.saveAndFlush(agent);

        // Get the agent
        restAgentMockMvc
            .perform(get(ENTITY_API_URL_ID, agent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agent.getId().intValue()))
            .andExpect(jsonPath("$.matricule").value(DEFAULT_MATRICULE))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.fonction").value(DEFAULT_FONCTION));
    }

    @Test
    @Transactional
    void getNonExistingAgent() throws Exception {
        // Get the agent
        restAgentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAgent() throws Exception {
        // Initialize the database
        agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agent
        Agent updatedAgent = agentRepository.findById(agent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAgent are not directly saved in db
        em.detach(updatedAgent);
        updatedAgent
            .matricule(UPDATED_MATRICULE)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .telephone(UPDATED_TELEPHONE)
            .fonction(UPDATED_FONCTION);
        AgentDTO agentDTO = agentMapper.toDto(updatedAgent);

        restAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAgentToMatchAllProperties(updatedAgent);
    }

    @Test
    @Transactional
    void putNonExistingAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAgentWithPatch() throws Exception {
        // Initialize the database
        agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agent using partial update
        Agent partialUpdatedAgent = new Agent();
        partialUpdatedAgent.setId(agent.getId());

        partialUpdatedAgent.matricule(UPDATED_MATRICULE).fonction(UPDATED_FONCTION);

        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgent))
            )
            .andExpect(status().isOk());

        // Validate the Agent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAgent, agent), getPersistedAgent(agent));
    }

    @Test
    @Transactional
    void fullUpdateAgentWithPatch() throws Exception {
        // Initialize the database
        agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agent using partial update
        Agent partialUpdatedAgent = new Agent();
        partialUpdatedAgent.setId(agent.getId());

        partialUpdatedAgent
            .matricule(UPDATED_MATRICULE)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .telephone(UPDATED_TELEPHONE)
            .fonction(UPDATED_FONCTION);

        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgent))
            )
            .andExpect(status().isOk());

        // Validate the Agent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentUpdatableFieldsEquals(partialUpdatedAgent, getPersistedAgent(partialUpdatedAgent));
    }

    @Test
    @Transactional
    void patchNonExistingAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAgent() throws Exception {
        // Initialize the database
        agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the agent
        restAgentMockMvc
            .perform(delete(ENTITY_API_URL_ID, agent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return agentRepository.count();
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

    protected Agent getPersistedAgent(Agent agent) {
        return agentRepository.findById(agent.getId()).orElseThrow();
    }

    protected void assertPersistedAgentToMatchAllProperties(Agent expectedAgent) {
        assertAgentAllPropertiesEquals(expectedAgent, getPersistedAgent(expectedAgent));
    }

    protected void assertPersistedAgentToMatchUpdatableProperties(Agent expectedAgent) {
        assertAgentAllUpdatablePropertiesEquals(expectedAgent, getPersistedAgent(expectedAgent));
    }
}
