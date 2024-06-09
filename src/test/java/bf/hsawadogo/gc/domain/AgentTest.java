package bf.hsawadogo.gc.domain;

import static bf.hsawadogo.gc.domain.AgentTestSamples.*;
import static bf.hsawadogo.gc.domain.DemandeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bf.hsawadogo.gc.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AgentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Agent.class);
        Agent agent1 = getAgentSample1();
        Agent agent2 = new Agent();
        assertThat(agent1).isNotEqualTo(agent2);

        agent2.setId(agent1.getId());
        assertThat(agent1).isEqualTo(agent2);

        agent2 = getAgentSample2();
        assertThat(agent1).isNotEqualTo(agent2);
    }

    @Test
    void demandeTest() throws Exception {
        Agent agent = getAgentRandomSampleGenerator();
        Demande demandeBack = getDemandeRandomSampleGenerator();

        agent.addDemande(demandeBack);
        assertThat(agent.getDemandes()).containsOnly(demandeBack);
        assertThat(demandeBack.getAgent()).isEqualTo(agent);

        agent.removeDemande(demandeBack);
        assertThat(agent.getDemandes()).doesNotContain(demandeBack);
        assertThat(demandeBack.getAgent()).isNull();

        agent.demandes(new HashSet<>(Set.of(demandeBack)));
        assertThat(agent.getDemandes()).containsOnly(demandeBack);
        assertThat(demandeBack.getAgent()).isEqualTo(agent);

        agent.setDemandes(new HashSet<>());
        assertThat(agent.getDemandes()).doesNotContain(demandeBack);
        assertThat(demandeBack.getAgent()).isNull();
    }
}
