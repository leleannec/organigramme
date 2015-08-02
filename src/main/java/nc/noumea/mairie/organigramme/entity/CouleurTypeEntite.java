package nc.noumea.mairie.organigramme.entity;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;

/**
 * Modélise la liaison entre un type d'entité (venant d'ADS) et une couleur.
 * 
 * @author AgileSoft.NC
 */
@Entity
public class CouleurTypeEntite extends AbstractEntity {

	@Version
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_couleur_type_entite")
	@SequenceGenerator(name = "s_couleur_type_entite", sequenceName = "s_couleur_type_entite", allocationSize = 1)
	Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Integer getVersion() {
		return this.version;
	}

	@Column(length = 1)
	@NotNull
	String couleurEntite;

	@Column(length = 1)
	@NotNull
	String couleurTexte;

	@Column
	Long idTypeEntite;

	@Override
	public String getLibelleCourt() {
		return "Couleur entité " + couleurEntite + "; couleur texte entité " + couleurTexte;
	}

	public String getCouleurEntite() {
		return couleurEntite;
	}

	public void setCouleurEntite(String couleurEntite) {
		this.couleurEntite = couleurEntite;
	}

	public String getCouleurTexte() {
		return couleurTexte;
	}

	public void setCouleurTexte(String couleurTexte) {
		this.couleurTexte = couleurTexte;
	}

	public Long getIdTypeEntite() {
		return idTypeEntite;
	}

	public void setIdTypeEntite(Long idTypeEntite) {
		this.idTypeEntite = idTypeEntite;
	}
}
