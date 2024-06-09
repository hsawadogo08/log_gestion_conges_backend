package bf.hsawadogo.gc.service.dto;

import bf.hsawadogo.gc.domain.enumeration.Etat;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link bf.hsawadogo.gc.domain.Demande} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeDTO implements Serializable {

    private Long id;

    private String motif;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private Etat etat;

    private AgentDTO agent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemandeDTO)) {
            return false;
        }

        DemandeDTO demandeDTO = (DemandeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, demandeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeDTO{" +
            "id=" + getId() +
            ", motif='" + getMotif() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", etat='" + getEtat() + "'" +
            ", agent=" + getAgent() +
            "}";
    }
}
