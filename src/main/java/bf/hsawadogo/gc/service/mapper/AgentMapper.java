package bf.hsawadogo.gc.service.mapper;

import bf.hsawadogo.gc.domain.Agent;
import bf.hsawadogo.gc.service.dto.AgentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {}
