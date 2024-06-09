package bf.hsawadogo.gc.domain;

import static bf.hsawadogo.gc.domain.AgentTestSamples.*;
import static bf.hsawadogo.gc.domain.DemandeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bf.hsawadogo.gc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DemandeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Demande.class);
        Demande demande1 = getDemandeSample1();
        Demande demande2 = new Demande();
        assertThat(demande1).isNotEqualTo(demande2);

        demande2.setId(demande1.getId());
        assertThat(demande1).isEqualTo(demande2);

        demande2 = getDemandeSample2();
        assertThat(demande1).isNotEqualTo(demande2);
    }

    @Test
    void agentTest() throws Exception {
        Demande demande = getDemandeRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        demande.setAgent(agentBack);
        assertThat(demande.getAgent()).isEqualTo(agentBack);

        demande.agent(null);
        assertThat(demande.getAgent()).isNull();
    }
}
