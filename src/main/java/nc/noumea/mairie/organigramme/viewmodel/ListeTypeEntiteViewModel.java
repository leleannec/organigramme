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

import nc.noumea.mairie.organigramme.core.services.AuthentificationService;
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractListeViewModel;
import nc.noumea.mairie.organigramme.core.ws.AdsWSConsumer;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.entity.CouleurTypeEntite;
import nc.noumea.mairie.organigramme.services.CouleurTypeEntiteService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

@VariableResolver(DelegatingVariableResolver.class)
public class ListeTypeEntiteViewModel extends AbstractListeViewModel<TypeEntiteDto> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final String CREATE_OR_EDIT_TYPE_ENTITE_VIEW = "/layout/createOrEditTypeEntite.zul";

	//@formatter:off
	@WireVariable	AdsWSConsumer 				adsWSConsumer;
	@WireVariable	CouleurTypeEntiteService 	couleurTypeEntiteService;
	@WireVariable	ReturnMessageService 		returnMessageService;
	@WireVariable 	AuthentificationService		authentificationService;
	//@formatter:on

	@Override
	@Init(superclass = true)
	public void init() {
		super.init();
		refreshListe();
	}

	@Command
	@NotifyChange("entity")
	public void createTypeEntite() throws InstantiationException, IllegalAccessException {

		if (!authentificationService.getCurrentUser().isAdministrateur()) {
			return;
		}
		
		ouvrePopupCreation(CREATE_OR_EDIT_TYPE_ENTITE_VIEW);
	}
	
	@Command
	@NotifyChange("entity")
	public void editTypeEntite() throws InstantiationException, IllegalAccessException {

		if(this.entity == null) {
			return;
		}
		
		if (!authentificationService.getCurrentUser().isAdministrateur()) {
			return;
		}
		
		initPopupEdition(this.entity, CREATE_OR_EDIT_TYPE_ENTITE_VIEW);
	}
	
	@GlobalCommand
	public void refreshListeTypeEntiteDto() {
		refreshListe();
	}

	@Command
	public void deleteTypeEntite() {

		if (!authentificationService.getCurrentUser().isAdministrateur()) {
			return;
		}
 
		final TypeEntiteDto typeEntiteDtoASupprimer = this.entity;

		Messagebox.show("Êtes-vous sur de vouloir supprimer ce type d'entité ?", "Confirmation", new Messagebox.Button[] { Messagebox.Button.YES,
				Messagebox.Button.NO }, Messagebox.QUESTION, new EventListener<Messagebox.ClickEvent>() {
			@Override
			public void onEvent(ClickEvent evt) {
				if (evt.getName().equals("onYes")) {

					ReturnMessageDto returnMessageDto = adsWSConsumer.deleteTypeEntite(typeEntiteDtoASupprimer);
					if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
						return;
					}

					CouleurTypeEntite couleurTypeEntite = couleurTypeEntiteService.findByIdTypeEntite(typeEntiteDtoASupprimer.getId());
					if (couleurTypeEntite != null) {
						couleurTypeEntiteService.delete(couleurTypeEntite);
					}
					refreshListe();
					BindUtils.postGlobalCommand(null, null, "creeArbreEtRafraichiClient", null);
				}
			}
		});
	}

	@Override
	public void refreshListe() {
		this.listeEntity.clear();
		this.listeEntity.addAll(adsWSConsumer.getListeTypeEntite());

		for (TypeEntiteDto typeEntiteDto : this.listeEntity) {
			CouleurTypeEntite couleurTypeEntite = couleurTypeEntiteService.findByIdTypeEntite(typeEntiteDto.getId());
			if (couleurTypeEntite != null) { 
				typeEntiteDto.setCouleurEntite(couleurTypeEntite.getCouleurEntite());
				typeEntiteDto.setCouleurTexte(couleurTypeEntite.getCouleurTexte());
			}
		}
		
		notifyChange("listeEntity");
	}
}
