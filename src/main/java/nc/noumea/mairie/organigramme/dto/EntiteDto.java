package nc.noumea.mairie.organigramme.dto;

/*
 * #%L
 * Logiciel de Gestion des Organigrammes de la Ville de Nouméa
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 Mairie de Nouméa
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.organigramme.core.dto.AbstractEntityDto;
import nc.noumea.mairie.organigramme.core.utility.JsonDateDeserializer;
import nc.noumea.mairie.organigramme.core.utility.JsonDateSerializer;
import nc.noumea.mairie.organigramme.core.utility.MessageErreur;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.enums.Transition;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.util.CollectionUtils;
import org.zkoss.zhtml.Li;

import flexjson.JSON;

@XmlRootElement
public class EntiteDto extends AbstractEntityDto {

	private Integer			idEntite;

	@Column(length = 20)
	private String			sigle;
	private String			label;

	@Column(length = 60)
	private String			labelCourt;
	private TypeEntiteDto	typeEntite;
	private String			codeServi;
	private List<EntiteDto>	enfants;
	private EntiteDto		entiteParent;
	private EntiteDto		entiteRemplacee;

	private Integer			idStatut;
	private Integer			idAgentCreation;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date			dateCreation;
	private Integer			idAgentModification;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date			dateModification;
	private String			refDeliberationActif;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date			dateDeliberationActif;
	private String			refDeliberationInactif;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date			dateDeliberationInactif;

	private Li				li;
	private Statut			statut;
	private Integer			idAgentSuppression;

	public EntiteDto() {
		enfants = new ArrayList<>();
	}

	public EntiteDto(EntiteDto entite) {
		mapEntite(entite);

		for (EntiteDto n : entite.getEnfants()) {
			this.enfants.add(new EntiteDto(n));
		}
	}

	public EntiteDto mapEntite(EntiteDto entite) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.labelCourt = entite.getLabelCourt();
		this.typeEntite = entite.getTypeEntite();
		this.codeServi = entite.getCodeServi();
		this.enfants = new ArrayList<>();
		this.entiteParent = null == entite.getEntiteParent() ? null : new EntiteDto(entite.getEntiteParent());
		this.entiteRemplacee = null == entite.getEntiteRemplacee() ? null : new EntiteDto(entite.getEntiteRemplacee());
		this.idEntite = entite.getIdStatut();
		this.idAgentCreation = entite.getIdAgentCreation();
		this.dateCreation = entite.getDateCreation();
		this.idAgentModification = entite.getIdAgentModification();
		this.dateModification = entite.getDateModification();
		this.refDeliberationActif = entite.getRefDeliberationActif();
		this.dateDeliberationActif = entite.getDateDeliberationActif();
		this.refDeliberationInactif = entite.getRefDeliberationInactif();
		this.dateDeliberationInactif = entite.getDateDeliberationInactif();

		return this;
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelCourt() {
		return labelCourt;
	}

	public void setLabelCourt(String labelCourt) {
		this.labelCourt = labelCourt;
	}

	public TypeEntiteDto getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(TypeEntiteDto typeEntite) {
		this.typeEntite = typeEntite;
	}

	public EntiteDto getEntiteParent() {
		return entiteParent;
	}

	public void setEntiteParent(EntiteDto entiteParent) {
		this.entiteParent = entiteParent;
	}

	public EntiteDto getEntiteRemplacee() {
		return entiteRemplacee;
	}

	public void setEntiteRemplacee(EntiteDto entiteRemplacee) {
		this.entiteRemplacee = entiteRemplacee;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	public void setIdStatut(Integer idStatut) {
		this.idStatut = idStatut;
	}

	public String getCodeServi() {
		return codeServi;
	}

	public void setCodeServi(String codeServi) {
		this.codeServi = codeServi;
	}

	public List<EntiteDto> getEnfants() {
		return enfants;
	}

	public void setEnfants(List<EntiteDto> enfants) {
		this.enfants = enfants;
	}

	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}

	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Integer getIdAgentModification() {
		return idAgentModification;
	}

	public void setIdAgentModification(Integer idAgentModification) {
		this.idAgentModification = idAgentModification;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getRefDeliberationActif() {
		return refDeliberationActif;
	}

	public void setRefDeliberationActif(String refDeliberationActif) {
		this.refDeliberationActif = refDeliberationActif;
	}

	public Date getDateDeliberationActif() {
		return dateDeliberationActif;
	}

	public void setDateDeliberationActif(Date dateDeliberationActif) {
		this.dateDeliberationActif = dateDeliberationActif;
	}

	public String getRefDeliberationInactif() {
		return refDeliberationInactif;
	}

	public void setRefDeliberationInactif(String refDeliberationInactif) {
		this.refDeliberationInactif = refDeliberationInactif;
	}

	public Date getDateDeliberationInactif() {
		return dateDeliberationInactif;
	}

	public void setDateDeliberationInactif(Date dateDeliberationInactif) {
		this.dateDeliberationInactif = dateDeliberationInactif;
	}

	@JSON(include = false)
	public Li getLi() {
		return li;
	}

	public void setLi(Li li) {
		this.li = li;
	}

	@JSON(include = false)
	public Integer getIdAgentSuppression() {
		return idAgentSuppression;
	}

	public void setIdAgentSuppression(Integer idAgentSuppression) {
		this.idAgentSuppression = idAgentSuppression;
	}

	@JSON(include = false)
	public Statut getStatut() {
		return statut;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}

	@Override
	@JSON(include = false)
	public String getLibelleCourt() {
		return label;
	}

	@Override
	@JSON(include = false)
	public Long getId() {
		if (this.getIdEntite() == null) {
			return null;
		}

		return new Long(this.getIdEntite().toString());
	}

	@Override
	@JSON(include = false)
	public Integer getVersion() {
		return null;
	}

	@Override
	public List<MessageErreur> construitListeMessageErreur() {

		List<MessageErreur> result = super.construitListeMessageErreur();

		if (StringUtils.isBlank(this.getSigle())) {
			result.add(new MessageErreur("Le sigle est obligatoire"));
		}

		if (StringUtils.isBlank(this.getLabel())) {
			result.add(new MessageErreur("Le libellé est obligatoire"));
		}

		if (StringUtils.isBlank(this.getLabelCourt())) {
			result.add(new MessageErreur("Le libellé court est obligatoire"));
		}

		if (this.getTypeEntite() == null) {
			result.add(new MessageErreur("Le type est obligatoire"));
		}

		return result;
	}

	public boolean hasChildren() {
		return !CollectionUtils.isEmpty(this.getEnfants());
	}

	/**
	 * @return la liste des transitions autorisées pour cette entité, en tenant compte du statut courant de l'entité et de règles de gestion, ne retourne jamais
	 *         null.
	 */
	@JSON(include = false)
	public List<Transition> getListeTransitionAutorise() {

		// construit la liste de toutes les transitions possibles depuis le statut source
		List<Transition> listeTransition = new ArrayList<>();
		for (Transition transition : Transition.values()) {
			boolean transitionDepuisStatutCourant = this.getStatut().equals(transition.getStatutSource());
			if (transitionDepuisStatutCourant) {

				// On se le statut de l'entité parent
				if (this.getEntiteParent() != null) {
					this.getEntiteParent().setStatut(Statut.getStatutById(this.getEntiteParent().getIdStatut()));
				}

				boolean statutParentNotPrevisionOrInactif = this.getEntiteParent() != null && this.getEntiteParent().getStatut() != Statut.PREVISION
						&& this.getEntiteParent().getStatut() != Statut.INACTIF;
				if (statutParentNotPrevisionOrInactif) {
					if (transition.getStatut() == Statut.ACTIF) {
						listeTransition.add(transition);
					} else if (transition.getStatut() == Statut.INACTIF) {
						// tous les noeuds descendants doivent être inactifs
						List<Statut> listeStatut = new ArrayList<Statut>();
						listeStatut.add(Statut.INACTIF);
						if (tousEnfantEnStatut(listeStatut, this.getEnfants())) {
							listeTransition.add(transition);
						}
					} else if (transition.getStatut() == Statut.TRANSITOIRE) {
						// tous les noeuds descendants doivent être transitoire ou inactifs
						List<Statut> listeStatut = new ArrayList<Statut>();
						listeStatut.add(Statut.TRANSITOIRE);
						listeStatut.add(Statut.INACTIF);
						if (tousEnfantEnStatut(listeStatut, this.getEnfants())) {
							listeTransition.add(transition);
						}
					}
				}
			}
		}

		return listeTransition;
	}

	/**
	 * Teste si tous les enfants sont obligatoirement dans les statuts passés en paramètre
	 * @param listeStatut : la liste des statuts qu'on souhaite tester
	 * @return true si tous les enfants sont dans ces statuts, false sinon
	 */
	public boolean tousEnfantEnStatut(List<Statut> listeStatut, List<EntiteDto> listeEnfant) {
		for (EntiteDto entiteDto : listeEnfant) {
			if (!listeStatut.contains(entiteDto.getStatut())) {
				return false;
			}
			return tousEnfantEnStatut(listeStatut, entiteDto.getEnfants());
		}

		return true;
	}

	/**
	 * @param nouveauStatut nouveau statut du projet
	 * @return true si le statut a vraiment changé
	 */
	public boolean updateStatut(Statut nouveauStatut) {
		if (nouveauStatut == null) {
			return false; // ne devrait pas arriver
		}
		if (this.statut != nouveauStatut) {
			this.statut = nouveauStatut;
			return true;
		}
		return false;
	}

	@JSON(include = false)
	public boolean isPrevision() {
		return this.getStatut() != null && this.getStatut() == Statut.PREVISION;
	}

	@JSON(include = false)
	public boolean isTransitoireOuInactif() {
		return this.getStatut() != null && (this.getStatut() == Statut.TRANSITOIRE || this.getStatut() == Statut.INACTIF);
	}

	@JSON(include = false)
	public boolean isInactif() {
		return this.getStatut() != null && this.getStatut() == Statut.INACTIF;
	}

	@JSON(include = false)
	public boolean isFeuille() {
		return CollectionUtils.isEmpty(this.getEnfants());
	}

	@JSON(include = false)
	public String getSigleWithLibelleStatut() {
		if (this.getStatut() != null && this.getStatut() != Statut.ACTIF) {
			return this.sigle + " (" + this.getStatut().getLibelle() + ")";
		}
		return this.sigle;
	}

	@Override
	public String toString() {
		return this.getSigle();
	}
}
