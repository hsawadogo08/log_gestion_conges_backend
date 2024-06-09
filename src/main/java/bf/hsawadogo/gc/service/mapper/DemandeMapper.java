package bf.hsawadogo.gc.service.mapper;

import bf.hsawadogo.gc.domain.Agent;
import bf.hsawadogo.gc.domain.Demande;
import bf.hsawadogo.gc.service.dto.AgentDTO;
import bf.hsawadogo.gc.service.dto.DemandeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Demande} and its DTO {@link DemandeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DemandeMapper extends EntityMapper<DemandeDTO, Demande> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    DemandeDTO toDto(Demande s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "matricule", source = "matricule")
    @Mapping(target = "nom", source = "nom")
    @Mapping(target = "prenom", source = "prenom")
    @Mapping(target = "telephone", source = "telephone")
    @Mapping(target = "fonction", source = "fonction")
    AgentDTO toDtoAgentId(Agent agent);
}
