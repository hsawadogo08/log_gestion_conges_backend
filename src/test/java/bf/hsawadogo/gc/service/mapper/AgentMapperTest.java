package bf.hsawadogo.gc.service.mapper;

import static bf.hsawadogo.gc.domain.AgentAsserts.*;
import static bf.hsawadogo.gc.domain.AgentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentMapperTest {

    private AgentMapper agentMapper;

    @BeforeEach
    void setUp() {
        agentMapper = new AgentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAgentSample1();
        var actual = agentMapper.toEntity(agentMapper.toDto(expected));
        assertAgentAllPropertiesEquals(expected, actual);
    }
}
