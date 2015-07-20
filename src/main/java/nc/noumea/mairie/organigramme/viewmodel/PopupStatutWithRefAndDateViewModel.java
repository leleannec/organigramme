package nc.noumea.mairie.organigramme.viewmodel;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.viewmodel.AbstractPopupViewModel;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.enums.Transition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@VariableResolver(DelegatingVariableResolver.class)
@Init(superclass = true)
public class PopupStatutWithRefAndDateViewModel extends AbstractPopupViewModel<EntiteDto> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Transition	transition;
	private String			refDeliberation;
	private Date			dateDeliberation;

	@AfterCompose 
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		setPopup((Window) Selectors.iterable(view, "#popupStatutWithRefAndDate").iterator().next());
	}
	
	public Transition getTransition() {
		return transition;
	}

	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	public String getRefDeliberation() {
		return refDeliberation;
	}

	public void setRefDeliberation(String refDeliberation) {
		this.refDeliberation = refDeliberation;
	}

	public Date getDateDeliberation() {
		return dateDeliberation;
	}

	public void setDateDeliberation(Date dateDeliberation) {
		this.dateDeliberation = dateDeliberation;
	}

	@GlobalCommand
	@NotifyChange("*")
	public void ouvrirPopupStatutWithRefAndDate(@BindingParam("entity") EntiteDto entity, @BindingParam("transition") Transition transition)
			throws InstantiationException, IllegalAccessException {
		setEntity(entity);
		setTransition(transition);
        getPopup().doModal();
	}

	@Command
	public void saveStatutWithRefAndDate() {

		if(!validerChampObligatoire()) {
			return;
		}

		if (transition.getStatut() == Statut.ACTIF) {
			entity.setDateDeliberationActif(dateDeliberation);
			entity.setRefDeliberationActif(refDeliberation);
		} else if (transition.getStatut() == Statut.INACTIF || transition.getStatut() == Statut.TRANSITOIRE) {
			entity.setDateDeliberationInactif(dateDeliberation);
			entity.setRefDeliberationInactif(refDeliberation);
		}

		Map<String, Object> args = new HashMap<>();
		args.put("entity", entity);
		args.put("transition", transition);
		args.put("popup", getPopup());
		BindUtils.postGlobalCommand(null, null, "saveStatutWithRefAndDateGenerique", args);
	}

	private boolean validerChampObligatoire() {
		if (transition.getStatut() != Statut.TRANSITOIRE) {
			// Lors d'un passage en statut transitoire la date et la référence ne sont pas obligatoires
			List<String> listeMessageErreur = new ArrayList<String>();
			if (StringUtils.isBlank(refDeliberation)) {
				listeMessageErreur.add("La référence de la délibération est obligatoire");
			}

			if (dateDeliberation == null) {
				listeMessageErreur.add("La date de la délibération est obligatoire");
			}

			if (!CollectionUtils.isEmpty(listeMessageErreur)) {
				Messagebox.show(StringUtils.join(listeMessageErreur, "\n"), "Erreur", Messagebox.OK, Messagebox.EXCLAMATION);
				return false;
			}
		}
		
		return true;
	}

	public String getCssLabelAndDate() {
		if (transition == null) {
			return "";
		}

		if (transition.getStatut() == Statut.ACTIF || transition.getStatut() == Statut.INACTIF) {
			return "mandatory";
		}

		return "";
	}
}
