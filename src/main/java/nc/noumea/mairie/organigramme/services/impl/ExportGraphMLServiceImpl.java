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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.services.ExportGraphMLService;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.zkoss.zul.Filedownload;

@Service("exportGraphMLService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ExportGraphMLServiceImpl implements ExportGraphMLService {

	public void exportGraphMLFromEntite(EntiteDto entiteDto, Map<String, Boolean> mapIdLiOuvert) {
		Filedownload.save(exportGraphML(entiteDto, mapIdLiOuvert), null, "exportGraphML.graphml");
	}
	
	private byte[] exportGraphML(EntiteDto entiteDto, Map<String, Boolean> mapIdLiOuvert) {

		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement("graphml");
		Document document = factory.createDocument(root);
		document.setXMLEncoding("utf-8");

		initHeader(root);
		Element graph = initRoot(root);
		buildGraphMlTree(graph, entiteDto, mapIdLiOuvert); 

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
		root.addAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		root.add(new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		root.add(new Namespace("y", "http://www.yworks.com/xml/graphml"));
		root.addElement("key").addAttribute("attr.name", "sigle").addAttribute("attr.type", "string").addAttribute("for", "node").addAttribute("id", "d4");
		root.addElement("key").addAttribute("attr.name", "label").addAttribute("attr.type", "string").addAttribute("for", "node").addAttribute("id", "d5");
		root.addElement("key").addAttribute("yfiles.type", "nodegraphics").addAttribute("for", "node").addAttribute("id", "d6");
		root.addElement("key").addAttribute("yfiles.type", "edgegraphics").addAttribute("for", "edge").addAttribute("id", "d7");
	}

	/**
	 * Recursive method to build graphml nodes and edges for the entire tree
	 * @param graph : le graph général
	 * @param entiteDto : l'entité dto à construire
	 * @param mapIdLiOuvert : permet de savoir quel entité est dépliée
	 */
	protected void buildGraphMlTree(Element graph, EntiteDto entiteDto, Map<String, Boolean> mapIdLiOuvert) {
		
		String couleurEntite = entiteDto.getTypeEntite() != null ? entiteDto.getTypeEntite().getCouleurEntite() : "#FFFFCF";
		String couleurTexte = entiteDto.getTypeEntite() != null ? entiteDto.getTypeEntite().getCouleurTexte() : "#000000";
		String forme = "roundrectangle";
		
		Element el = graph.addElement("node").addAttribute("id", String.valueOf(entiteDto.getId())); 
		el.addElement("data").addAttribute("key", "d4").setText(entiteDto.getSigle());
		el.addElement("data").addAttribute("key", "d5").setText(entiteDto.getLabel());

		Element elD6 = el.addElement("data").addAttribute("key", "d6");
		Element elGenericNode = elD6.addElement("y:ShapeNode");
		elGenericNode.addElement("y:Geometry").addAttribute("height", "40.0").addAttribute("width", "80.0"); 
		String sigleSplit = OrganigrammeUtil.splitByNumberAndSeparator(entiteDto.getSigle(), 8, "\n");
		elGenericNode.addElement("y:NodeLabel").addAttribute("textColor", couleurTexte).setText(sigleSplit);
		
		if(entiteDto.getStatut() == Statut.PREVISION) 	{ 	elGenericNode.addElement("y:BorderStyle").addAttribute("type", "dashed"); }
		if(entiteDto.getStatut() == Statut.TRANSITOIRE) { 	forme = "parallelogram"; }
		if(entiteDto.getStatut() == Statut.INACTIF) 	{ 	couleurEntite = "#909090"; couleurTexte = "#000000";}
		
		elGenericNode.addElement("y:Fill").addAttribute("color", couleurEntite); 
		elGenericNode.addElement("y:Shape").addAttribute("type", forme);
		
		//Si l'entité est dépliée, on affiche ses enfants
		if(mapIdLiOuvert.get(entiteDto.getLi().getId())) {
			for (EntiteDto enfant : entiteDto.getEnfants()) {
				buildGraphMlTree(graph, enfant, mapIdLiOuvert);
				genereEdge(graph, entiteDto, enfant);
			}
		}
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
