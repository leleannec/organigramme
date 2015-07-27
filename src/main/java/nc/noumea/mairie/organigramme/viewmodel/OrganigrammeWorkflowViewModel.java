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
import java.util.HashMap;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.viewmodel.AbstractEditViewModel;
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractViewModel;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.enums.FiltreStatut;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.enums.Transition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

/**
 * Classe de gestion du workflow, couplée à un OrganigrammeViewModel.
 * 
 * @author AgileSoft.NC
 */
public class OrganigrammeWorkflowViewModel extends AbstractEditViewModel<EntiteDto> implements Serializable {

	private static final long		serialVersionUID	= 1L;

	private OrganigrammeViewModel	organigrammeViewModel;

	public static Logger			log					= LoggerFactory.getLogger(OrganigrammeWorkflowViewModel.class);

	public OrganigrammeWorkflowViewModel(OrganigrammeViewModel organigrammeViewModel) {
		this.organigrammeViewModel = organigrammeViewModel;
	}

	public EntiteDto entity() {
		return organigrammeViewModel.getEntity();
	}

	public void passerTransition(Transition transition) {
		if (transition == null) {
			return;
		}
		if (transition.getStatutSource() != entity().getStatut()) {
			AbstractViewModel.showErrorPopup("Bug (de rafraîchissement des boutons de changement de statut) : le statut de l'entité est "
					+ entity().getStatut() + ", mais la transition demandée est " + transition);
			return;
		}
		// @formatter:off
		switch (transition) {
			
			case TRANSITOIRE:								executerTransitionTransitoire();							break;
			case ACTIF_APRES_PREVISION:						executerTransitionActifApresPrevision();					break;
			case INACTIF_APRES_TRANSITOIRE: 				executerTransitionInactifApresTransitoire();				break;
			case INACTIF_APRES_ACTIF: 						executerTransitionInactifApresActif();						break;
			default:										executerTransitionGenericAvecConfirmation(transition, null);break;
		}
		// @formatter:on
	}

	private void executerTransitionTransitoire() {
		executerTransitionWithRefAndDate(Transition.TRANSITOIRE, Statut.TRANSITOIRE);
	}

	private void executerTransitionActifApresPrevision() {
		executerTransitionWithRefAndDate(Transition.ACTIF_APRES_PREVISION, Statut.ACTIF);
	}

	private void executerTransitionInactifApresTransitoire() {
		executerTransitionWithRefAndDate(Transition.INACTIF_APRES_TRANSITOIRE, Statut.INACTIF);
	}

	private void executerTransitionInactifApresActif() {
		executerTransitionWithRefAndDate(Transition.INACTIF_APRES_ACTIF, Statut.INACTIF);
	}

	private void executerTransitionWithRefAndDate(Transition transition, Statut statut) {
		verifieTransitionPossible(transition);

		Map<String, Object> args = new HashMap<>();
		args.put("entity", entity());
		args.put("transition", transition);

		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("entity", entity);
		Executions.createComponents("/layout/popupStatutWithRefAndDate.zul", null, arguments);

		BindUtils.postGlobalCommand(null, null, "ouvrirPopupStatutWithRefAndDate", args);
	}

	private void verifieTransitionPossible(Transition transition) {
		if (entity().getStatut() != transition.getStatutSource()) {
			throw new IllegalArgumentException("le passage de transition " + transition + " n'est pas autorisé depuis le statut " + entity().getStatut());
		}
	}

	/**
	 * Transition générique simple
	 * @param transition transition à passer
	 * @return true si la transition est bien passé
	 */
	public boolean executerTransitionGeneric(Transition transition) {
		verifieTransitionPossible(transition);
		if (this.organigrammeViewModel.organigrammeService.updateStatut(this.entity(), transition.getStatut())) {
			// On recharge l'arbre complet d'ADS et on rafraichi le client. Ainsi on est sur d'avoir une version bien à jour
			this.organigrammeViewModel.entiteDtoRoot = this.organigrammeViewModel.adsWSConsumer.getCurrentTreeWithVDNRoot();

			// On force l'affichage du statut pour pouvoir voir cette entité dans son nouvel état
			if (transition.getStatut().equals(Statut.TRANSITOIRE)) {
				this.organigrammeViewModel.setSelectedFiltreStatut(FiltreStatut.ACTIF_TRANSITOIRE);
			}
			if (transition.getStatut().equals(Statut.INACTIF)) {
				this.organigrammeViewModel.setSelectedFiltreStatut(FiltreStatut.ACTIF_TRANSITOIRE_INACTIF);
			}
			this.organigrammeViewModel.creeArbreEtRafraichiClient();
			return true;
		}

		return false;
	}

	/**
	 * Transition générique simple, après confirmation
	 * 
	 * @param transition transition à passer
	 * @param message optionnel (si null, un message générique est proposé)
	 */
	private void executerTransitionGenericAvecConfirmation(Transition transition, String messageConfirmation) {
		if (messageConfirmation == null) {
			messageConfirmation = "Confirmez-vous le passage de l'entité en statut " + transition.getStatut().getLibelle() + " ?";
		}
		final Transition transitionAExecuter = transition;

		Messagebox.show(messageConfirmation, "Confirmation", new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION,
				new EventListener<Messagebox.ClickEvent>() {
					@Override
					public void onEvent(ClickEvent evt) {
						if (evt.getName().equals("onYes")) {
							executerTransitionGeneric(transitionAExecuter);
						}
					}
				});
	}
}
