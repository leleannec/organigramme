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

import java.util.List;

import nc.noumea.mairie.organigramme.core.dto.AbstractEntityDto;
import nc.noumea.mairie.organigramme.core.utility.MessageErreur;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;

import org.apache.commons.lang.StringUtils;

import flexjson.JSON;

public class TypeEntiteDto extends AbstractEntityDto {

	Long						id;
	String						label;
	String						couleurEntite		= "#FFFFCF";
	String						couleurTexte		= "#000000";
	boolean						actif				= true;
	boolean 					entiteAs400;

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	@JSON(include=false)
	public String getLibelleCourt() {
		return label;
	}

	@Override
	@JSON(include=false)
	public Integer getVersion() {
		return this.getVersion();
	}

	@Override
	public Long getId() {
		return id;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public boolean isEntiteAs400() {
		return entiteAs400;
	}

	public void setEntiteAs400(boolean entiteAs400) {
		this.entiteAs400 = entiteAs400;
	}

	@JSON(include=false)
	public String getCouleurEntite() {
		//#16887 : on force la couleur en #FFFFCF si elle n'est pas renseignée
		if(couleurEntite == null) {
			return "#FFFFCF";
		}
		return couleurEntite;
	}

	public void setCouleurEntite(String couleurEntite) {
		this.couleurEntite = couleurEntite;
	}
	
	@JSON(include=false)
	public String getCouleurTexte() {
		return couleurTexte;
	}

	public void setCouleurTexte(String couleurTexte) {
		this.couleurTexte = couleurTexte;
	}

	@JSON(include=false)
	public String getStyleEntite() {
		return "height: 15px; width: 100%; border: 1px solid; background-color: " + (this.getCouleurEntite() != null ? this.getCouleurEntite() : "#FFFFCF") + ";";
	}
	
	@JSON(include=false)
	public String getStyleTexte() {
		return "height: 15px; width: 100%; border: 1px solid; background-color: " + (this.getCouleurTexte() != null ? this.getCouleurTexte() : "#000000") + ";";
	}

	@JSON(include=false)
	public String getLabelWithActifInactif() {
		return label + (this.actif ? "" : " (inactif)");
	}
	
	@Override
	public List<MessageErreur> construitListeMessageErreur() {

		List<MessageErreur> result = super.construitListeMessageErreur();

		if (StringUtils.isBlank(this.getLabel())) {
			result.add(new MessageErreur("Le libellé est obligatoire"));
		}

		if (StringUtils.isBlank(this.getCouleurEntite())) {
			result.add(new MessageErreur("La couleur de l'entité est obligatoire"));
		} else if (!OrganigrammeUtil.isCodeCouleurHtmlValide(this.getCouleurEntite())) {
			result.add(new MessageErreur("Vous devez renseigner une couleur de l'entité au format HTML (exemple : #FEAABD)"));
		}
		
		if (StringUtils.isBlank(this.getCouleurTexte())) {
			result.add(new MessageErreur("La couleur du texte de l'entité est obligatoire"));
		} else if (!OrganigrammeUtil.isCodeCouleurHtmlValide(this.getCouleurTexte())) {
			result.add(new MessageErreur("Vous devez renseigner une couleur du texte de l'entité au format HTML (exemple : #FEAABD)"));
		}

		return result;
	}
}
