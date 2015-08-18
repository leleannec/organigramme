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

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;

import nc.noumea.mairie.organigramme.core.utility.DateUtil;
import nc.noumea.mairie.organigramme.core.ws.SirhWSConsumer;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.InfoEntiteDto;
import nc.noumea.mairie.organigramme.dto.InfoFichePosteDto;
import nc.noumea.mairie.organigramme.enums.FiltreStatut;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.services.ExportGraphMLService;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.zkoss.zul.Filedownload;

@Service("exportGraphMLService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ExportGraphMLServiceImpl implements ExportGraphMLService {

	@Autowired
	SirhWSConsumer sirhWSConsumer;

	public void exportGraphMLFromEntite(EntiteDto entiteDto, FiltreStatut filtreStatut,
			Map<String, Boolean> mapIdLiOuvert) throws IOException {
		String nomFichier = "Export-" + entiteDto.getSigle() + "-" + DateUtil.formatDateForFile(new Date())
				+ ".graphml";
		Filedownload.save(exportGraphML(entiteDto, filtreStatut, mapIdLiOuvert), null, nomFichier);
	}

	private byte[] exportGraphML(EntiteDto entiteDto, FiltreStatut filtreStatut, Map<String, Boolean> mapIdLiOuvert)
			throws IOException {

		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement("graphml");
		Document document = factory.createDocument(root);
		document.setXMLEncoding("utf-8");

		Element graph = initRoot(root);
		initHeader(root);
		buildGraphMlTree(graph, entiteDto, mapIdLiOuvert, filtreStatut);

		ByteArrayOutputStream os_writer = new ByteArrayOutputStream();
		try {
			BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(os_writer, "UTF-8"));
			document.write(wtr);
			wtr.flush();
			wtr.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return os_writer.toByteArray();
	}

	private Element initRoot(Element root) {
		Element graph = root.addElement("graph");
		graph.addAttribute("id", "1");
		graph.addAttribute("edgedefault", "undirected");
		return graph;
	}

	private void initHeader(Element root) {
		root.addAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		root.addAttribute("xsi:schemaLocation",
				"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		root.add(new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		root.add(new Namespace("y", "http://www.yworks.com/xml/graphml"));
		root.addElement("key").addAttribute("attr.name", "sigle").addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d4");
		root.addElement("key").addAttribute("attr.name", "libellé").addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d5");
		root.addElement("key").addAttribute("yfiles.type", "nodegraphics").addAttribute("for", "node")
				.addAttribute("id", "d6");
		root.addElement("key").addAttribute("yfiles.type", "edgegraphics").addAttribute("for", "edge")
				.addAttribute("id", "d7");
		root.addElement("key").addAttribute("attr.name", "sigle + libellé").addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d8");
		root.addElement("key").addAttribute("attr.name", "sigle + FDP").addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d9");
		root.addElement("key").addAttribute("attr.name", "sigle + libellé + FDP").addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d10");
		root.addElement("key").addAttribute("attr.name", "sigle + libellé + N° FDP")
				.addAttribute("attr.type", "string").addAttribute("for", "node").addAttribute("id", "d11");
		root.addElement("key").addAttribute("yfiles.type", "resources").addAttribute("for", "graphml")
				.addAttribute("id", "d12");
	}

	/**
	 * Recursive method to build graphml nodes and edges for the entire tree
	 * 
	 * @param graph
	 *            : l'élément graph
	 * @param entiteDto
	 *            : l'entité dto à construire : doit-on exporter les fiches de
	 *            postes ?
	 * @param mapIdLiOuvert
	 *            : permet de savoir quel entité est dépliée
	 * @throws IOException
	 *             : si le logo n'a pas pu être ajouté au graphml
	 */
	protected void buildGraphMlTree(Element graph, EntiteDto entiteDto, Map<String, Boolean> mapIdLiOuvert,
			FiltreStatut filtreStatut) throws IOException {

		String couleurEntite = entiteDto.getTypeEntite() != null ? entiteDto.getTypeEntite().getCouleurEntite()
				: "#FFFFCF";
		String couleurTexte = entiteDto.getTypeEntite() != null ? entiteDto.getTypeEntite().getCouleurTexte()
				: "#000000";
		String forme = "roundrectangle";

		Element el = graph.addElement("node").addAttribute("id", String.valueOf(entiteDto.getId()));
		el.addElement("data").addAttribute("key", "d4").setText(entiteDto.getSigle());
		el.addElement("data").addAttribute("key", "d5").setText(entiteDto.getLabel());
		el.addElement("data").addAttribute("key", "d8").setText(entiteDto.getSigle() + "\n" + entiteDto.getLabel());

		String libelleFdp = getLibelleCaseWithLibelleFdp(entiteDto, !mapIdLiOuvert.get(entiteDto.getIdLi()), true);
		String numeroFdp = getLibelleCaseWithLibelleFdp(entiteDto, !mapIdLiOuvert.get(entiteDto.getIdLi()), false);

		String sigleEtLibelleFdp = entiteDto.getSigle() + "\n" + libelleFdp;
		String sigletEtLibelleEtLibelleFdp = entiteDto.getSigle() + "\n" + entiteDto.getLabel() + "\n" + libelleFdp;
		String sigletEtLibelleEtNumeroFdp = entiteDto.getSigle() + "\n" + entiteDto.getLabel() + "\n" + numeroFdp;

		el.addElement("data").addAttribute("key", "d9").setText(sigleEtLibelleFdp);
		el.addElement("data").addAttribute("key", "d10").setText(sigletEtLibelleEtLibelleFdp);
		el.addElement("data").addAttribute("key", "d11").setText(sigletEtLibelleEtNumeroFdp);

		Element elD6 = el.addElement("data").addAttribute("key", "d6");
		Element elGenericNode = elD6.addElement("y:ShapeNode");
		elGenericNode.addElement("y:Geometry").addAttribute("height", "40.0").addAttribute("width", "80.0");

		if (entiteDto.getStatut() == Statut.PREVISION) {
			elGenericNode.addElement("y:BorderStyle").addAttribute("type", "dashed")
					.addAttribute("color", couleurTexte);
		}
		if (entiteDto.getStatut() == Statut.TRANSITOIRE) {
			forme = "parallelogram";
		}
		if (entiteDto.getStatut() == Statut.INACTIF) {
			couleurEntite = "#909090";
			couleurTexte = "#000000";
		}

		elGenericNode.addElement("y:Fill").addAttribute("color", couleurEntite);
		elGenericNode.addElement("y:Shape").addAttribute("type", forme);

		// Si l'entité est dépliée, on affiche ses enfants
		if (mapIdLiOuvert.get(entiteDto.getIdLi())) {
			for (EntiteDto enfant : entiteDto.getEnfants()) {

				if (filtreStatut.equals(FiltreStatut.TOUS)
						|| filtreStatut.getListeStatut().contains(enfant.getStatut())) {
					buildGraphMlTree(graph, enfant, mapIdLiOuvert, filtreStatut);
					genereEdge(graph, entiteDto, enfant);
				}
			}
		}

		elGenericNode.addElement("y:NodeLabel").addAttribute("textColor", couleurTexte).setText(entiteDto.getSigle());

		ajoutLogoMairie(graph);
	}

	/**
	 * Permet d'ajouter le logo de la mairie au fichier d'export
	 * 
	 * @param graph
	 *            : l'élément graph
	 * @throws IOException
	 *             : si le logo ne peux pas être lu ou ajouté au fichier
	 */
	private void ajoutLogoMairie(Element graph) throws IOException {
		Element elKeyResource = graph.getParent().addElement("data").addAttribute("key", "d12");
		Element elResource = elKeyResource.addElement("y:Resources").addElement("y:Resource");
		elResource.addAttribute("id", "1").addAttribute("type", "java.awt.image.BufferedImage");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("logo-mairie.jpg");
		BufferedImage img = ImageIO.read(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", baos);
		baos.flush();
		String encodedImage = Base64.encodeBase64String(baos.toByteArray());
		baos.close();
		elResource.setText(encodedImage);

		Element el = graph.addElement("node").addAttribute("id", "0");
		Element elD6 = el.addElement("data").addAttribute("key", "d6");
		Element elImageNode = elD6.addElement("y:ImageNode");
		elImageNode.addElement("y:Geometry").addAttribute("height", "136.3").addAttribute("width", "143.0")
				.addAttribute("x", "256.0").addAttribute("y", "-291.0");
		elImageNode.addElement("y:Image").addAttribute("alphaImage", "true").addAttribute("refid", "1");
	}

	/**
	 * Renvoie le libellé à mettre dans la case Yed
	 * 
	 * @param entiteDto
	 *            : l'entité
	 * @param withEntiteChildren
	 *            : selon si l'entité est dépliée ou non on affiche les FDP des
	 *            enfants ou non
	 * @param libelleFdpSinonNumero
	 *            : si true, on affichera les libellés des FDP, si false on
	 *            affichera les numéros des FDP séparés par une virgule
	 * @return le libellé à mettre dans la case Yed
	 */
	private String getLibelleCaseWithLibelleFdp(EntiteDto entiteDto, boolean withEntiteChildren,
			boolean libelleFdpSinonNumero) {

		String result = "";

		InfoEntiteDto infoEntiteDto = sirhWSConsumer.getInfoFDPByEntite(entiteDto.getIdEntite(), withEntiteChildren);
		if (infoEntiteDto != null && !CollectionUtils.isEmpty(infoEntiteDto.getListeInfoFDP())) {

			if (libelleFdpSinonNumero) {
				for (InfoFichePosteDto infoFichePosteDto : infoEntiteDto.getListeInfoFDP()) {
					if (new Double(infoFichePosteDto.getNbFDP()).equals(infoFichePosteDto.getTauxETP())) {
						result += infoFichePosteDto.getTitreFDP() + " (" + infoFichePosteDto.getNbFDP() + ")\n";
					} else {
						result += infoFichePosteDto.getTitreFDP() + " (" + infoFichePosteDto.getNbFDP() + " / "
								+ infoFichePosteDto.getTauxETP() + " ETP)\n";
					}
				}
			} else {
				for (InfoFichePosteDto infoFichePosteDto : infoEntiteDto.getListeInfoFDP()) {
					// TODO
					// result += infoFichePosteDto.getNumero() + "\n";
				}
			}
		}

		return result;
	}

	private void genereEdge(Element graph, EntiteDto entiteDto, EntiteDto enfant) {
		Element edge = graph.addElement("edge");
		edge.addAttribute("source", String.valueOf(entiteDto.getId()));
		edge.addAttribute("target", String.valueOf(enfant.getId()));
		Element elD7 = edge.addElement("data").addAttribute("key", "d7");
		Element elGenericEdge = elD7.addElement("y:PolyLineEdge");
		elGenericEdge.addElement("y:Arrows").addAttribute("type", "plain");
	}
}
