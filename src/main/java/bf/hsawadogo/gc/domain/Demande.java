package bf.hsawadogo.gc.domain;

import bf.hsawadogo.gc.domain.enumeration.Etat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Demande.
 */
@Entity
@Table(name = "demande")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Demande implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "motif")
    private String motif;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat")
    private Etat etat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "demandes" }, allowSetters = true)
    private Agent agent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Demande id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotif() {
        return this.motif;
    }

    public Demande motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDateDebut() {
        return this.dateDebut;
    }

    public Demande dateDebut(LocalDate dateDebut) {
        this.setDateDebut(dateDebut);
        return this;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return this.dateFin;
    }

    public Demande dateFin(LocalDate dateFin) {
        this.setDateFin(dateFin);
        return this;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Etat getEtat() {
        return this.etat;
    }

    public Demande etat(Etat etat) {
        this.setEtat(etat);
        return this;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Demande agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Demande)) {
            return false;
        }
        return getId() != null && getId().equals(((Demande) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Demande{" +
            "id=" + getId() +
            ", motif='" + getMotif() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", etat='" + getEtat() + "'" +
            "}";
    }
}
