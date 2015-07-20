package nc.noumea.mairie.organigramme.services.impl;

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
import java.util.List;

import nc.noumea.mairie.organigramme.core.services.impl.GenericServiceImpl;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.zkoss.zk.ui.util.Clients;

@Service("returnMessageService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReturnMessageServiceImpl extends GenericServiceImpl<ReturnMessageDto> implements ReturnMessageService {

	public boolean gererReturnMessage(ReturnMessageDto returnMessageDto) {
		if (returnMessageDto == null) {
			return true;
		}

		if (returnMessageDto.getErrors().size() > 0 || returnMessageDto.getInfos().size() > 0) {
			List<String> listeInformation = new ArrayList<String>();
			List<String> listeErreur = new ArrayList<String>();

			for (String error : returnMessageDto.getErrors()) {
				listeErreur.add(error);
			}
			for (String info : returnMessageDto.getInfos()) {
				listeInformation.add(info);
			}

			if (!CollectionUtils.isEmpty(listeErreur)) {
				showNotificationErreur(StringUtils.join(listeErreur, "\n"));
				return false;
			}

			if (!CollectionUtils.isEmpty(listeInformation)) {
				showNotificationStandard(StringUtils.join(listeInformation, "\n"));
			}
		}

		return true;
	}

	public void showNotificationStandard(String message) {
		Clients.showNotification(message, "info", null, "top_center", 0);
	}

	public void showNotificationErreur(String message) {
		Clients.showNotification(message, "error", null, "top_center", 0);
	}
}