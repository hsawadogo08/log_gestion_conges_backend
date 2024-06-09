package bf.hsawadogo.gc.service;

import bf.hsawadogo.gc.domain.Agent;
import bf.hsawadogo.gc.repository.AgentRepository;
import bf.hsawadogo.gc.service.dto.AgentDTO;
import bf.hsawadogo.gc.service.mapper.AgentMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link bf.hsawadogo.gc.domain.Agent}.
 */
@Service
@Transactional
public class AgentService {

    private final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final AgentRepository agentRepository;

    private final AgentMapper agentMapper;

    public AgentService(AgentRepository agentRepository, AgentMapper agentMapper) {
        this.agentRepository = agentRepository;
        this.agentMapper = agentMapper;
    }

    /**
     * Save a agent.
     *
     * @param agentDTO the entity to save.
     * @return the persisted entity.
     */
    public AgentDTO save(AgentDTO agentDTO) {
        log.debug("Request to save Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        agent = agentRepository.save(agent);
        return agentMapper.toDto(agent);
    }

    /**
     * Update a agent.
     *
     * @param agentDTO the entity to save.
     * @return the persisted entity.
     */
    public AgentDTO update(AgentDTO agentDTO) {
        log.debug("Request to update Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        agent = agentRepository.save(agent);
        return agentMapper.toDto(agent);
    }

    /**
     * Partially update a agent.
     *
     * @param agentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AgentDTO> partialUpdate(AgentDTO agentDTO) {
        log.debug("Request to partially update Agent : {}", agentDTO);

        return agentRepository
            .findById(agentDTO.getId())
            .map(existingAgent -> {
                agentMapper.partialUpdate(existingAgent, agentDTO);

                return existingAgent;
            })
            .map(agentRepository::save)
            .map(agentMapper::toDto);
    }

    /**
     * Get all the agents.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AgentDTO> findAll() {
        log.debug("Request to get all Agents");
        return agentRepository.findAll().stream().map(agentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one agent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AgentDTO> findOne(Long id) {
        log.debug("Request to get Agent : {}", id);
        return agentRepository.findById(id).map(agentMapper::toDto);
    }

    /**
     * Delete the agent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Agent : {}", id);
        agentRepository.deleteById(id);
    }
}
