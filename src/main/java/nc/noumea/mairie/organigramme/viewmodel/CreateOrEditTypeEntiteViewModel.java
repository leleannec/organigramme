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
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractPopupViewModel;
import nc.noumea.mairie.organigramme.core.ws.AdsWSConsumer;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.entity.CouleurTypeEntite;
import nc.noumea.mairie.organigramme.services.CouleurTypeEntiteService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;

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
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Window;

@Init(superclass = true)
@VariableResolver(DelegatingVariableResolver.class)
public class CreateOrEditTypeEntiteViewModel extends AbstractPopupViewModel<TypeEntiteDto> implements Serializable {

	private static final long serialVersionUID = 1L;

	@WireVariable
	AdsWSConsumer adsWSConsumer;

	// @formatter:off
	@WireVariable
	CouleurTypeEntiteService couleurTypeEntiteService;
	@WireVariable
	ReturnMessageService returnMessageService;
	@WireVariable
	AuthentificationService authentificationService;

	// @formatter:on

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		setPopup((Window) Selectors.iterable(view, "#createUpdateTypeEntite").iterator().next());
		setEntity(new TypeEntiteDto());
	}

	@GlobalCommand
	@NotifyChange("entity")
	public void ouvrePopupEditionTypeEntiteDto(@BindingParam("entity") TypeEntiteDto typeEntiteDto) {
		setEntity(typeEntiteDto);
		getPopup().doModal();
	}

	@Command
	@NotifyChange("entity")
	public void saveOrUpdate() {

		if (!authentificationService.getCurrentUser().isAdministrateur()) {
			return;
		}

		if (showErrorPopup(this.entity)) {
			return;
		}

		ReturnMessageDto returnMessageDto = adsWSConsumer.saveOrUpdateTypeEntite(this.entity);
		if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
			return;
		}

		CouleurTypeEntite couleurTypeEntite = null;

		if (this.entity.getId() != null) {
			couleurTypeEntite = couleurTypeEntiteService.findByIdTypeEntite(this.entity.getId());
		}

		if (couleurTypeEntite != null) {
			couleurTypeEntite.setCouleurEntite(this.entity.getCouleurEntite());
			couleurTypeEntite.setCouleurTexte(this.entity.getCouleurTexte());
			couleurTypeEntiteService.update(couleurTypeEntite);
		} else {
			couleurTypeEntite = new CouleurTypeEntite();
			couleurTypeEntite.setIdTypeEntite(new Long(returnMessageDto.getId()));
			couleurTypeEntite.setCouleurEntite(this.entity.getCouleurEntite());
			couleurTypeEntite.setCouleurTexte(this.entity.getCouleurTexte());
			couleurTypeEntiteService.save(couleurTypeEntite);
		}

		postGlobalCommandRefreshListe();
		BindUtils.postGlobalCommand(null, null, "creeArbreEtRafraichiClient", null);
		getPopup().detach();
	}
}
