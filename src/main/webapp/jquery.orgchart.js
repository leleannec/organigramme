/**
 * JQuery Organisation Chart Plugin.
 *
 * Author: Mark Lee
 * Copyright (C)2013-2015 Caprica Software Limited
 * http://www.capricasoftware.co.uk
 *
 * Contributions by: Paul Lautman <paul.lautman at gmail.com>
 *
 * This software is licensed under the Creative Commons Attribution-ShareAlike 3.0 License,
 * see here for license terms:
 *
 *     http://creativecommons.org/licenses/by-sa/3.0
 */ 

var $selectedEntite; 
var $root;
var DELAY = 700, clicks = 0, timer = null;

function refreshOrganigramme() { 
	$("#organigramme-root").orgChart({container: $("#chart")});
};
 
function refreshOrganigrammeSuiteAjout(idLi, idLiParent) { 
	$("#organigramme-root").orgChart({container: $("#chart"), editEntiteId: idLi, editEntiteParentId: idLiParent});
};

function refreshOrganigrammeWithoutSelectedEntite() { 
	$("#organigramme-root").orgChart({container: $("#chart"), removeEditEntite: true}); 
}; 

function refreshOrganigrammeSuiteZoom() { 
	$("#organigramme-root").orgChart({container: $("#chart"), removeEditEntite: true, replie: true});
	deplierTout();
}; 

function refreshOrganigrammeReplie() { 
	$("#organigramme-root").orgChart({container: $("#chart"), removeEditEntite: true, replie: true});
};

function deplierTout() {
	zAu.send(new zk.Event(zk.Widget.$('$organigramme'), 'onClickToutDeplier', null)); 
	refreshOrganigrammeReplie();
	$(".entite").trigger("deplierReplierEntite");
};

function replierTout() {
	zAu.send(new zk.Event(zk.Widget.$('$organigramme'), 'onClickToutReplier', null));
	refreshOrganigrammeReplie(); 
}; 

function expandEntiteFromIdDiv(id) {
	var $entite = $("div#" + id);
	expandEntite($entite);
};

function expandEntite($entiteDiv) { 
    var $row = $entiteDiv.closest("tr");
    if ($row.next("tr").is(":visible")) {
    	$entiteDiv.addClass('collapsed'); 
    	$row.nextAll("tr").hide();
        $row.removeClass("shownChildren").addClass("hiddenChildren");
    }
    else {
    	$entiteDiv.removeClass('collapsed');
    	$row.removeClass("hiddenChildren").addClass("shownChildren");
        $row.nextAll("tr").show();
    } 
    
    zAu.send(new zk.Event(zk.Widget.$('$organigramme'), 'onClickFlecheDeplierReplier', $entiteDiv.attr("id"))); 
};

function goToByScroll(id){
	deplierTout();
	refreshOrganigrammeSuiteAjout(id, null);
	var hauteur = $("#chart").parent().height() / 2 - 30;  
	var largeur = $("#panel-entier").parent().width() / 2 - 25; 
    $('#chart').scrollTo($("div#"+id), 800, {offset: function() { return {top:-hauteur, left:-largeur};}});
}; 
 
(function($) {
	
    $.fn.orgChart = function(options) {
        var opts = $.extend({}, $.fn.orgChart.defaults, options);

        return this.each(function() {
        	
        	if(opts.removeEditEntite) { 
            	removeEditEntite(opts);
            }
        	
        	var $previousChart = $('#chart');
            var $chartSource = $(this);
            // Clone the source list hierarchy so levels can be non-destructively removed if needed
            // before creating the chart
            $this = $chartSource.clone();
            if (opts.levels > -1) {
                $this.find("ul").andSelf().filter(function() {return $chartSource.parents("ul").length+1 > opts.levels;}).remove();
            }
            // Store the original element
            $this.data("chart-source", $chartSource);
            // Build the chart...
            var $container = $("<div class='" + opts.chartClass + "'/>");
            $container.addClass("interactive");
            // The chart may be sourced from either a "ul", or an "li" element...
            if ($this.is("ul")) {
                $root = $this.find("li:first");
            }
            else if ($this.is("li")) {
                $root = $this;
            }
            if ($root) {
                buildEntite($root, $container, 0, 0, opts, $previousChart);
                // Special case for any hyperlink anchor in the chart to prevent the click on the entity itself from propagating
                $container.find("div.entite a").click(function(evt) {
                    evt.stopImmediatePropagation();
                });
                if(opts.replace) {
                    opts.container.empty();
                }
                opts.container.append($container);
            }  
             
            //On a recréé un nouvel arbre, on set à nouveau le selectedEntite pour qu'il pointe sur le nouvel élèment correspondant
            if($selectedEntite != null) {
            	$selectedEntite = $("div#" + $selectedEntite[0].id);
            }
             
            //Si on vient d'ajouter une nouvelle entité, on la selectionne
            if(opts.editEntiteId != "") {
            	var $entiteToEdit = $("div#" + opts.editEntiteId);
            	var $liToEdit = $("li#" + opts.editEntiteId); 
            	
            	//On déplie le parent si ce n'est pas le cas pour voir l'entité qu'on vient de créer
            	if(opts.editEntiteParentId != null && opts.editEntiteParentId != "") {
            		var $entiteParent = $("div#" + opts.editEntiteParentId);
                	var $row = $entiteParent.closest("tr");
                	if(!($row.next("tr").is(":visible"))) {
                		expandEntite($entiteParent);
                    }
            	}
            	
            	editEntite($entiteToEdit, opts); 
            	
            	//On simule l'événement click zk pour passer côté serveur et charger le nouvel objet
            	var liWidget = zk.Widget.$($liToEdit);
            	zAu.send(new zk.Event(liWidget, "onClick", null));
            }
            
            //On calcule les hauteurs pour s'adapter à toutes les résolutions
            var hauteurFenetre = $("#panel-entier").parent().height();
            var hauteurToolbar = $("#div-toolbar").parent().height();
            
        	$('#chart').height(hauteurFenetre - hauteurToolbar - 10);
            $('#panel-entier').height(hauteurFenetre);
            $(window).trigger('resize'); 
        }); 
    };

    $.fn.orgChart.defaults = {
        container  : $("body"),
        depth      : -1,
        levels     : -1,
        stack      : false,
        chartClass : "orgChart",
        hoverClass : "hover",
        entiteText   : function($entite) {return $entite.clone().children("ul,li").remove().end().html();},
        copyClasses: true,
        copyData   : true,
        copyStyles : true,
        copyTitle  : true,
        replace    : true, 
        replie	   : false,
        removeEditEntite : false,
        editEntiteId : "",
        editEntiteParentId : ""
    };

    function editEntite($entite, opts) {
    	if($selectedEntite != null) {
    		//Si l'entité est déjà selectionnée on ne fait rien 
    		if($entite.get(0).id == $selectedEntite.get(0).id) {
    			return;  
    		}
    		
    		$selectedEntite.removeClass("edit");
    	}
    	$entite.addClass("edit");
    	$selectedEntite = $entite;
    	opts.editEntiteId = ""; 
    	opts.editEntiteParentId = "";
    };
    
    function removeEditEntite(opts) {
    	if($selectedEntite != null) {
    		$selectedEntite.removeClass("edit");
    		$selectedEntite = null;
    		opts.editEntiteId = ""; 
        	opts.editEntiteParentId = "";
    	}
    };
    
    function buildEntite($entite, $appendTo, level, index, opts, $previousChart) {
        var $table = $("<table cellpadding='0' cellspacing='0' border='0'/>");
        var $tbody = $("<tbody/>");

        // Make this entity...
        var $entiteRow = $("<tr/>").addClass("entites");
        var $entiteCell = $("<td/>").addClass("entite").attr("colspan", 2);
        var $childEntites = $entite.children("ul:first").children("li");
        if ($childEntites.length > 1) {
            $entiteCell.attr("colspan", $childEntites.length*2);
        }

        var $adjunct = $entite.children("adjunct").eq(0);
        if ($adjunct.length > 0) {
            var $adjunctDiv = $("<div>").addClass("adjunct entite").addClass("level"+level).addClass("entite"+index).addClass("level"+level+"-entite"+index).append(opts.entiteText($adjunct));
            $adjunctDiv.appendTo($entiteCell);
            var $linkDiv = $("<div>").addClass("adjunct-link");
            $linkDiv.appendTo($entiteCell);
            $adjunct.remove();
        }

        var $heading = $("<h2>").html(opts.entiteText($entite));
        var $entiteDiv = $("<div>").addClass("entite").addClass("level"+level).addClass("entite"+index).addClass("level"+level+"-entite"+index).append($heading);

        // Copy classes from the source list to the chart entity
        if (opts.copyClasses) {
            $entiteDiv.addClass($entite.attr("class"));
        }

        // Copy data from the source list to the chart entity
        if (opts.copyData) {
            $entiteDiv.data($entite.data());
        }

        // Copy CSS styles from the source list to the chart entity
        if (opts.copyStyles) {
            $entiteDiv.attr("style", $entite.attr("style"));
        }

        // Copy the title attribute from the source list to the chart entity
        if (opts.copyTitle) {
            $entiteDiv.attr("title", $entite.attr("title"));
        }
        
        $entiteDiv.attr("id", $entite.attr("id"));
        $entiteDiv.data("orgchart-level", level).data("orgchart-entite", $entite);
        
        $entiteCell.append($entiteDiv);
        $entiteRow.append($entiteCell);
        $tbody.append($entiteRow); 
        
    	$entiteDiv.click(function(e) {  
    		clicks++;  

            if(clicks === 1) { 
                timer = setTimeout(function() {
                	 
                	if(e.target.classList[0] == "hasChildren") {
                		expandEntite($entiteDiv); 
                	}
                	else {
                		if($entiteDiv.hasClass("edit")) {
                			zAu.send(new zk.Event(zk.Widget.$('$organigramme'), 'onClickEntite', null));
                        	removeEditEntite(opts);  
                		}
                		else {
                			zAu.send(new zk.Event(zk.Widget.$('$organigramme'), 'onClickEntite', $entiteDiv.attr("id"))); 
                    		editEntite($entiteDiv, opts);  
                		}
                	}
                    clicks = 0;             //after action performed, reset counter
                }, DELAY);
            } else { 
            	clearTimeout(timer);		//prevent single-click action
                clicks = 0;             	//after action performed, reset counter
                zAu.send(new zk.Event(zk.Widget.$('$organigramme'), 'onDblClickEntite', $entiteDiv.attr("id"))); 
            }
        });
        
        $entiteDiv.dblclick(function(e) {
    		e.preventDefault();  			//cancel system double-click event
        });
         
        $entiteDiv.on("deplierReplierEntite", function(e) {
        	expandEntite($entiteDiv);
        	
        });
         
        if ($childEntites.length > 0) {
            if (opts.depth == -1 || level+1 < opts.depth) {
                var $downLineRow = $("<tr/>").addClass("lines");
                var $downLineCell = $("<td/>").attr("colspan", $childEntites.length*2);
                $downLineRow.append($downLineCell);

                var $downLineTable = $("<table cellpadding='0' cellspacing='0' border='0'>");
                $downLineTable.append("<tbody>");
                var $downLineLine = $("<tr/>").addClass("lines x");
                var $downLeft = $("<td>").addClass("line left");
                var $downRight = $("<td>").addClass("line right");
                $downLineLine.append($downLeft).append($downRight);
                $downLineTable.children("tbody").append($downLineLine);
                $downLineCell.append($downLineTable);

                $tbody.append($downLineRow);

                if ($childEntites.length > 0) {
                	var $infoEnfant = $("<div />").addClass("hasChildren");
                    $entiteDiv.append($infoEnfant);
                    $entiteDiv.hover(function() {$(this).addClass(opts.hoverClass);}, function() {$(this).removeClass(opts.hoverClass)});
                }

                // Recursively make child entitys...
                var $linesRow = $("<tr/>").addClass("lines v");
                $childEntites.each(function() {
                    var $left = $("<td/>").addClass("line left top");
                    var $right = $("<td/>").addClass("line right top");
                    $linesRow.append($left).append($right);
                });
                $linesRow.find("td:first").removeClass("top");
                $linesRow.find("td:last").removeClass("top");
                $tbody.append($linesRow);
                var $childEntitesRow = $("<tr/>");
                $childEntites.each(function(index) {
                    var $td = $("<td/>");
                    $td.attr("colspan", 2);
                    buildEntite($(this), $td, level+1, index, opts, $previousChart);
                    $childEntitesRow.append($td);
                });
                $tbody.append($childEntitesRow);
            }
            else if (opts.stack) {
                var $stackEntites = $childEntites.clone();
                var $list = $("<ul class='stack'>").append($stackEntites).addClass("level"+level).addClass("entite"+index).addClass("level"+level+"-entite"+index);
                var $stackContainer = $("<div class='stack-container'>").append($list);
                $entiteDiv.after($stackContainer); 
            }
        }
         
        if(opts.replie) {
        	$entiteRow.nextAll("tr").hide(); 
        	$entiteDiv.addClass('collapsed'); 
        } 
        
        //On prend l'ancien arbre et on remet les mêmes classes sur le nouveau (afin que toutes les entités qui étaient dépliées le soit
        //sur le nouveau aussi)
        var $previousEntite = $previousChart.find("#" + $entite[0].id); 
        if ($previousEntite.attr('class') != undefined) {
            var classList = $previousEntite.attr('class').split(/\s+/);
            $.each(classList, function(index,item) {
                if (item == 'collapsed') {
                    $entiteRow.nextAll('tr').hide(); 
                    $entiteRow.removeClass('shownChildren');
                    $entiteRow.addClass('hiddenChildren');
                    $entiteDiv.addClass('collapsed');
                } else {
                	//On ne recopie pas les anciens css de statut
                	if(item != 'hasChildren' && item != 'statut-actif' && item != 'statut-prevision' && item != 'statut-transitoire' && item != 'statut-inactif') { 
                		$entiteDiv.addClass(item); 
                	}
                }
            });
        }
         
        $table.append($tbody);
        $appendTo.append($table);
    };

})(jQuery);
