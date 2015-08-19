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
import javax.persistence.Table;
import javax.persistence.Version;

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;

/**
 * Modélise les entités favoris d'un utilisateur
 * 
 * @author AgileSoft.NC
 */
@Entity
@Table(name = "orga_entite_favoris")
public class EntiteFavoris extends AbstractEntity {

	@Version
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orga_s_entite_favoris")
	@SequenceGenerator(name = "orga_s_entite_favoris", sequenceName = "orga_s_entite_favoris", allocationSize = 1)
	Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Integer getVersion() {
		return this.version;
	}

	@Column
	Integer idAgent;

	@Column
	Integer idEntite;

	@Override
	public String getLibelleCourt() {
		return "IdAgent " + idAgent + "; idEntite " + idEntite;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}
}
