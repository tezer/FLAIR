//==================== FLAIR WEBRANKER ======================//
FLAIR.createNS("FLAIR.WEBRANKER");
FLAIR.createNS("FLAIR.WEBRANKER.CONSTANTS");
FLAIR.createNS("FLAIR.WEBRANKER.UTIL");
FLAIR.createNS("FLAIR.WEBRANKER.UTIL.TOGGLE");

//=================== FLAIR.WEBRANKER.CONSTANTS =============//
FLAIR.WEBRANKER.CONSTANTS.DEFAULT_NUM_RESULTS = 20;		// no of web results to crawl/process
FLAIR.WEBRANKER.CONSTANTS.DEFAULT_LANGUAGE = "ENGLISH";
FLAIR.WEBRANKER.CONSTANTS.HIGHLIGHT_COLORS = ["lightgreen", "lightblue", "lightpink", "lightcyan", "lightsalmon", "lightgrey", "lightyellow"];   

//=================== FLAIR.WEBRANKER.UTIL =============//
FLAIR.WEBRANKER.UTIL.formatDocLevel = function(level) {
    // turns a string "LEVEL-c" into "C1-C2"
    var l = level.substring(level.indexOf("-") + 1);
    l = l.toUpperCase();
    return (l + "1-" + l + "2");
};
FLAIR.WEBRANKER.UTIL.generateConstructionName = function(parent, name_to_show) {
    var name = name_to_show;
    if (parent !== null)
    {
	parent = parent.parentNode; // div panel-body
	if (parent !== null)
	{
	    parent = parent.parentNode; // div 
	    if (parent !== null) 
	    {
		var grandparent = parent.parentNode;
		parent = parent.id; // div panel-collapse collapse
		if (parent.indexOf("collapse_") > -1)
		{
		    parent = parent.substring(9);
		    // add parent_name to name
		    name = parent + "  > " + name;

		    if (grandparent !== null && grandparent.className.indexOf("panel-success") > -1)
		    {	// e.g. verbs
			// one more layer of constructs
			grandparent = grandparent.parentNode; // div panel-body
			if (grandparent !== null)
			{
			    grandparent = grandparent.parentNode; // div panel-collapse collapse
			    if (grandparent !== null)
			    {
				grandparent = grandparent.id;
				if (grandparent.indexOf("collapse_") > -1)
				{
				    grandparent = grandparent.substring(9);
				    // add parent_name to name
				    name = grandparent + "  > " + name;
				}
			    }
			}
		    }
		}
	    }
	}
    }
    return name.replace("_", " ");
};
FLAIR.WEBRANKER.UTIL.resetSlider = function(name) {
    if (name === "all")
    {
        $(".gradientSlider").each(function () {
            if ($(this).slider("option", "value") !== 0) {
                $(this).slider("option", "value", 0);
            }
        });
    } 
    else
    {
        $("#collapse_" + name + " div[id$='-gradientSlider']").each(function () {
            if ($(this).slider("option", "value") !== 0) {
                $(this).slider("option", "value", 0);
            }
        });
    }
};
FLAIR.WEBRANKER.UTIL.resetTextCharacteristics = function() {
    // reset the readability settings
    $("#LEVEL-a").prop('checked', true);
    $("#LEVEL-b").prop('checked', true);
    $("#LEVEL-c").prop('checked', true);
    // reset the length slider
    $(".lengthSlider").slider("value", 5);
};
FLAIR.WEBRANKER.UTIL.updateWaitDialog = function(content, cancellable) {
    document.getElementById("modal_waitIdle_body").innerHTML = content;
    if (cancellable === false)
	document.getElementById('modal_waitIdle_buttonCancel').style.visibility = 'hidden';
    else
	document.getElementById('modal_waitIdle_buttonCancel').style.visibility = 'visible';
};
FLAIR.WEBRANKER.UTIL.resetUI = function() {
    FLAIR.WEBRANKER.UTIL.TOGGLE.leftSidebar(false);
    FLAIR.WEBRANKER.UTIL.TOGGLE.rightSidebar(false);
    FLAIR.WEBRANKER.UTIL.TOGGLE.waitDialog(false);
    FLAIR.WEBRANKER.UTIL.resetSlider("all");
    $("#snapshot").html("<div id='empty_sidebar_info'>Click on a search result <br>to display text here.</div>");    
    document.getElementById("results_table").innerHTML = "<tr><td><br><br> Welcome to <b>FLAIR</b> - a tool that: <br><br><br> - searches the web for a topic of interest <br><br> - analyses the top 20 results for grammatical constructions and readability levels <br><br> - re-ranks the results according to your (pedagogical or learning) needs specified in the settings</td></tr>";
};

//=================== FLAIR.WEBRANKER.UTIL.TOGGLE =============//
FLAIR.WEBRANKER.UTIL.TOGGLE.constructionsList = function() {
    if (document.getElementById("show_all_constructions").hidden)
        document.getElementsByClassName("caret")[0].parentNode.className = "dropup";
    else
        document.getElementsByClassName("caret")[0].parentNode.className = "";

    document.getElementById("show_all_constructions").hidden = (!document.getElementById("show_all_constructions").hidden);
};
FLAIR.WEBRANKER.UTIL.TOGGLE.leftSidebar = function(show) {
    if (show)
	$("#wrapper").addClass("toggled");
    else
	$("#wrapper").removeClass("toggled");
};
FLAIR.WEBRANKER.UTIL.TOGGLE.rightSidebar = function(show) {
    if (show)
	$("#sidebar-wrapper-right").addClass("active");
    else
	$("#sidebar-wrapper-right").removeClass("active");
};
FLAIR.WEBRANKER.UTIL.TOGGLE.waitDialog = function(show) {
    if (show)
	$("#modal_WaitIdle").modal('show');
    else
	$("#modal_WaitIdle").modal('hide');
};
FLAIR.WEBRANKER.UTIL.TOGGLE.visualiserDialog = function(show) {
    if (show)
	$("#myModal_Visualize").modal('show');
    else
	$("#myModal_Visualize").modal('hide');
};

//=================== FLAIR.WEBRANKER =============//
FLAIR.WEBRANKER.STATE = function() {
    // PRIVATE VARS
    var query = "";
    var language = FLAIR.WEBRANKER.CONSTANTS.DEFAULT_LANGUAGE;
    var totalResults = FLAIR.WEBRANKER.CONSTANTS.DEFAULT_NUM_RESULTS;		// no of results to search for, updated with the result count returned by the server

    var parsedDocs = [];			    // collection of all the parsed docs sent by the server
    var displayedDocs = [];			    // collection of all the docs being displayed
    var filteredDocs = [];			    // collection of the docs indices (in the parsed docs collection) that shouldn't be displayed

    var selection = -1;				    // index of the current selection in the displayed docs collection

    var weightSettings_docLevel = [];		    // collection of weight data for the doc levels (A1-C2) and all constructions
    var weightSettings_constructions = [];	    // collection of weight data for grammatical constructions (### redundant?)

    var filteredConstructions = [];		    // collection of the grammatical constructions that are ignored when ranking

    var bParam = 0;				    // ### what's this? used with the doc length slider
    var kParam = 1.7;				    // ### what's this?
    
    var parsedVisData = "";			    // CSV string of the parsed docs' construction data, used for visualisation
    
    // PRIVATE INTERFACE
    var createWeightSettingPrototype = function() {
	var weightSetting = {
	    name: "",
	    weight: 0
	};
	return weightSetting;
    };
    var refreshWeightSettings = function() {
	weightSettings_docLevel = [];
	weightSettings_constructions = [];

	// doc levels
	var levels = document.getElementById("settings_levels").getElementsByTagName("input");
	for (var k = 0; k < levels.length; k++) 
	{
	    var weightSetting = createWeightSettingPrototype();
	    weightSetting.name = levels[k].id;
	    if (levels[k].checked)
		weightSetting.weight = 1;
	    else
		weightSetting.weight = 0;

	    weightSettings_docLevel.push(weightSetting);
	    weightSettings_constructions.push(weightSetting);
	}

	// rest of the constructions
	$(".gradientSlider").each(function () {
	    var w = $(this).slider("option", "value");
	    w = w / 5; // turn into the scale from 0 to 1

	    var n = this.id.substring(0, this.id.indexOf("-"));
	    var weightSetting = createWeightSettingPrototype();
	    weightSetting.name = n;
	    weightSetting.weight = w;
	    if (w !== 0)
		weightSettings_docLevel.push(weightSetting);

	    weightSettings_constructions.push(weightSetting);
	});    
    };
    var getHighlightedDocHTML = function(doc) {
	var newText = ""; // String
	var occs = []; // occurrences of THESE constructionS in this doc
	var color_count = 0;

	for (var i in weightSettings_docLevel)
	{ 
	    var name = weightSettings_docLevel[i]["name"];
	    var color = "lightyellow";

	    if (name.indexOf("LEVEL") < 0)
	    {
		// assign colors
		color = FLAIR.WEBRANKER.CONSTANTS.HIGHLIGHT_COLORS[color_count];
		color_count++;
	    }

	    for (var j in doc["highlights"]) 
	    {
		var o = doc["highlights"][j];
		if (o["construction"] === name && (weightSettings_docLevel[i]["weight"] < 0 || weightSettings_docLevel[i]["weight"] > 0))
		{
		    o["color"] = color;
		    var contains = false;
		    for (var k in occs) 
		    {
			var occ = occs[k];
			if (occ["construction"] === name && occ["start"] === o["start"] && occ["end"] === o["end"]) 
			{
			    contains = true;
			    break;
			}
		    }
		    if (!contains)
			occs.push(o);
		}
	    }
	}

	// // sort the occurrences based on their (end) indices
	var allIndices = []; // String[][]
	for (var j = 0; j < occs.length * 2; j++)
	    allIndices.push({});

	for (var i = 0; i < occs.length; i++) 
	{
	    var o = occs[i]; // Occurrence
	    var start = o["start"]; // int
	    var end = o["end"]; // int

	    var spanStart = "<span style='background-color:" + o["color"] + ";' title='" + o["construction"] + "'>"; // String
	    var spanEnd = "</span>"; // String

	    allIndices[i] = {"tag": spanStart, "index": (start + "-" + end)};
	    allIndices[occs.length * 2 - 1 - i] = {"tag": spanEnd, "index": end};
	}

	// sort allIndices - start and end in descending order
	// each element is an object with keys "tag" and "index"
	allIndices.sort(function (ind1, ind2) 
	{
	    var indexSt1 = (ind1["index"]).toString(); // String
	    var indexSt2 = (ind2["index"]).toString(); // String
	    var indexEnd1 = ""; // String
	    var indexEnd2 = ""; // String

	    var s1 = -5; // int
	    var s2 = -5; // int
	    var e1 = -5; // int
	    var e2 = -5; // int

	    // indexSt1 can be either of form start-end or just end
	    if (indexSt1.indexOf("-") > -1) 
	    {
		indexEnd1 = indexSt1.substring(indexSt1.indexOf("-") + 1);
		indexSt1 = indexSt1.substring(0, indexSt1.indexOf("-"));
	    }
	    if (indexSt2.indexOf("-") > -1) 
	    {
		indexEnd2 = indexSt2.substring(indexSt2.indexOf("-") + 1);
		indexSt2 = indexSt2.substring(0, indexSt2.indexOf("-"));
	    }

	    s1 = parseInt(indexSt1); // int
	    s2 = parseInt(indexSt2); // int

	    var sComp = s2 - s1; // int

	    if (sComp !== 0)
		return sComp;
	    else 
	    {
		if (indexEnd1.length > 0) 
		{
		    if (indexEnd2.length > 0) 
		    {
			e1 = parseInt(indexEnd1); // int
			e2 = parseInt(indexEnd2); // int

			var l1 = Math.abs(e1 - s1); // int
			var l2 = Math.abs(e2 - s2); // int

			return (l1 - l2);
		    } 
		    else
			return sComp;
		} 
		else if (indexEnd2.length > 0) 
		{
		    if (indexEnd1.length > 0) 
		    {
			e1 = parseInt(indexEnd1);
			e2 = parseInt(indexEnd2);

			var l1 = Math.abs(e1 - s1); // int
			var l2 = Math.abs(e2 - s2); // int

			return (l1 - l2);
		    }
		    else
			return sComp;
		} 
		else
		    return sComp;
	    }
	});

	// insert span tags into the text : start from the end
	var docText = doc["text"]; // String
	// get rid of html tags inside of text
	var tmp = document.createElement("DIV");
	tmp.innerHTML = docText;
	docText = tmp.textContent || tmp.innerText;
	docText.replace("\n","<br>");

	var prevStartInd = -1; // int // prev ind of a start-tag
	var prevEndInd = -1; // int // prev ind of a start-tag
	var prevConstruct = ""; // String
	var prevStartTag = ""; // String

	for (var ind in allIndices) 
	{
	    var insertHere = -10; // int
	    var curItem = (allIndices[ind]["index"]).toString(); // String
	    var tmpEnd = -1; // int
	    var tmpStart = -1; // int
	    // indexSt1 can be either of form start-end or just end
	    if (curItem.indexOf("-") > -1)
	    {
		tmpStart = parseInt(curItem.substring(0, curItem.indexOf("-")));
		tmpEnd = parseInt(curItem.substring(curItem.indexOf("-") + 1));
		insertHere = tmpStart;
	    }
	    else
		insertHere = parseInt(curItem);

	    var tag = allIndices[ind]["tag"]; // String
	    // show several constructions on mouseover, ONLY if they fully overlap (e.g., complex sentence, direct question)
	    if (tag.indexOf("<span") > -1)
	    {
		var toInsertBefore = ""; // String // to take care in more than 2 overlapping constructions
		if (prevStartInd === insertHere && prevEndInd === tmpEnd)
		{
		    tag = tag.substring(0, tag.indexOf("'>")) + ", " + prevConstruct + "'>";
		    insertHere += prevStartTag.length;
		    toInsertBefore = prevStartTag;
		}

		prevStartInd = parseInt(curItem.substring(0, curItem.indexOf("-"))); // int
		prevEndInd = parseInt(curItem.substring(curItem.indexOf("-") + 1)); // int
		prevStartTag = toInsertBefore + tag;
		prevConstruct = tag.substring(tag.indexOf("title='") + 7, tag.indexOf("'>"));
	    }

	    docText = docText.substring(0, insertHere) + tag + docText.substring(insertHere);
	}

	newText = docText;
	return newText;
    };
    
    // PUBLIC INTERFACE
    this.reset = function() {
	query = "";
	language = FLAIR.WEBRANKER.CONSTANTS.DEFAULT_LANGUAGE;
	totalResults = FLAIR.WEBRANKER.CONSTANTS.DEFAULT_NUM_RESULTS;
	
	parsedDocs = [];
	displayedDocs = [];
	filteredDocs = [];

	selection = -1;

	weightSettings_docLevel = [];
	weightSettings_constructions = [];

	filteredConstructions = [];

	bParam = 0;
	kParam = 1.7;
	
	parsedVisData = "";
    };
    this.displayDocText = function(index) {
	if (displayedDocs.length === 0)
	    return;
	else if (displayedDocs.length <= index)
	{
	    console.log("Invalid selection for text display. Must satisfy 0 < " + index + " < " + displayedDocs.length);
	    return;
	}

	var previousSelection = selection;
	selection = index;
	var doc = displayedDocs[index];

	document.getElementById("snapshot").innerHTML = "";
	// remove the highlight from the results
	if (previousSelection > -1 && document.getElementById("results_table").childNodes.length > 0)
	    $("#results_table tr:nth-child(" + (previousSelection + 1) + ")").css("background-color", "white");

	FLAIR.WEBRANKER.UTIL.TOGGLE.rightSidebar(true);

	if (document.getElementById("results_table").childNodes.length > 0)
	{
	    // highlight the corresponding result
	    $("#results_table tr:nth-child(" + (index + 1) + ")").css("background-color", "#fdf6e6");

	    var info_box_1 = "<table style='width:100%'><tr>";
	    if (doc.readabilityLevel !== null && doc.readabilityLevel.length > 3)
		info_box_1 += "<td class='text-cell'><b>" + FLAIR.WEBRANKER.UTIL.formatDocLevel(doc.readabilityLevel) + "</b></td>";
	    if (doc.numSents > 0)
		info_box_1 += "<td class='text-cell'>~" + doc.numSents + " sentences</td>";
	    if (doc.docLength > 0 || doc.numDeps > 0)
		info_box_1 += "<td class='text-cell'>~" + doc.numDeps + " words</td>";
	    info_box_1 += "</tr></table>";

	    var info_box_2 = "<table id='constructions-table'><thead><tr><td><b>Construct</b></td><td><b>Count</b></td><td><b>Weight</b></td></tr></thead><tbody>";
	    var info_box_3 = "<div id='show_all_constructions' hidden><table id='all_constructions_table' class='tablesorter'><thead><tr><td><b>Construct</b></td><td><b>Count</b></td><td><b>Relative Frequency %</b></td></tr></thead><tbody>";

	    var count_col = 0;
	    for (var i in weightSettings_constructions)
	    {
		var name = weightSettings_constructions[i]["name"];
		var name_to_show = name;
		var g = document.getElementById(weightSettings_constructions[i]["name"] + "-df");
		if (g !== null)
		{
		    name_to_show = g.parentNode.textContent;
		    if (name_to_show.indexOf("\n") > 0)
			name_to_show = name_to_show.substring(0, name_to_show.indexOf("\n"));

		    if (name_to_show.indexOf("(") > 0)
			name_to_show = name_to_show.substring(0, name_to_show.indexOf("("));

		    // one more layer of constructs
		    var parent_name = g.parentNode; // div 
		    if (parent_name !== null)
			name_to_show = FLAIR.WEBRANKER.UTIL.generateConstructionName(parent_name, name_to_show);
		}

		var all = doc["constructions"];
		for (var j in all)
		{
		    if (all[j] === name) 
		    {
			var ind = j;
			if ((weightSettings_constructions[i]["weight"] > 0 ||
			     weightSettings_constructions[i]["weight"] < 0))
			{
			    var col = "lightyellow";
			    if (count_col < FLAIR.WEBRANKER.CONSTANTS.HIGHLIGHT_COLORS.length)
			    {
				col = FLAIR.WEBRANKER.CONSTANTS.HIGHLIGHT_COLORS[count_col];
				count_col++;
			    }

			    info_box_2 += "<tr class='constructions_line'><td style='background-color:" + col + "'>" + name_to_show + "</td><td class='text-cell'>" + doc["frequencies"][ind] + "</td><td class='text-cell'>(" + weightSettings_constructions[i]["weight"] + ")</td></tr>";
			}

			info_box_3 += "<tr><td>" + name_to_show + "</td><td class='text-cell'>" + doc["frequencies"][ind] + "</td><td class='text-cell'>(" + ((Number(doc["relFrequencies"][ind])) * 100).toFixed(4) + ")</td></tr>";
		    }
		}
	    }

	    info_box_2 += "</tbody></table><br> <div id='info-highlights'></div>";
	    info_box_3 += "</tbody></table><br></div><br><br><br>";

	    // add a "copy text" button
	    var text_string = "<br>";
	    text_string += "<div id='sidebar_text' readonly>";
	    text_string += getHighlightedDocHTML(doc);
	    text_string += "</div><br>";

	    document.getElementById("snapshot").innerHTML = info_box_1 + text_string + info_box_2 + info_box_3;

	    var lines = document.getElementsByClassName("constructions_line");
	    if (lines.length > 0)
	    {
		document.getElementById("constructions-table").hidden = false;
		if (lines.length === 1)
		{
		    document.getElementById("info-highlights").innerHTML = "";
		    document.getElementById("info-highlights").innerHTML = "<div class='panel panel-default' style='text-align: center'><a href='javascript:FLAIR.WEBRANKER.UTIL.TOGGLE.constructionsList();' style='color:darkgrey;'><span class='caret'></span> all constructions <span class='caret'></span></a></div><br>";
		}
		else
		{
		    document.getElementById("info-highlights").innerHTML = "* Highlights may overlap. Mouse over a highlight to see a tooltip<br> with the names of all embedded constructions.<br><br>"
			    + "<div class='panel panel-default' style='text-align: center'><a href='javascript:FLAIR.WEBRANKER.UTIL.TOGGLE.constructionsList();' style='color:darkgreen;'><span class='caret'></span> all constructions <span class='caret'></span></a></div>";
		}
	    } 
	    else
	    {
		document.getElementById("info-highlights").innerHTML = "<button type='button' class='btn btn-warning' onclick='FLAIR.WEBRANKER.UTIL.TOGGLE.leftSidebar(true)' id='sidebar_grammar_button'>GRAMMAR SETTINGS</button>";
		document.getElementById("constructions-table").hidden = true;
	    }
	}

	var theVar = document.getElementById("all_constructions_table");
	if (theVar)
	    $("#all_constructions_table").tablesorter();
    };
    this.rerank = function() {
	bParam = ($(".lengthSlider").slider("option", "value")) / 10;
	if (bParam === null || bParam === undefined)
	    bParam = 5;

	if (parsedDocs.length === 0)
	    return;

	displayedDocs = [];
	refreshWeightSettings();

	var avDocLen = -1;

	for (var k = 0; k < parsedDocs.length; k++) 
	{
	    var doc = parsedDocs[k];
	    var appropriateDoc = true;

	    for (var j = 0; j < weightSettings_constructions.length; j++)
	    {
		if (weightSettings_constructions[j]["name"].indexOf("LEVEL") > -1) 
		{
		    if (weightSettings_constructions[j]["weight"] === 0) 
		    {
			$("#" + weightSettings_constructions[j]["name"]).prop('checked', false);

			if (doc["readabilityLevel"] === weightSettings_constructions[j]["name"]) 
			    appropriateDoc = false;
		    } 
		    else if (weightSettings_constructions[j]["weight"] === 1)
			$("#" + weightSettings_constructions[j]["name"]).prop('checked', true);
		}
	    }

	    doc["gramScore"] = 0.0;

	    // check if the doc contains excluded constructions
	    for (var t = 0; t < filteredConstructions.length; t++)
	    {
		var constr_ind = doc.constructions.indexOf(filteredConstructions[t]);
		if (doc.frequencies[constr_ind] > 0)
		    appropriateDoc = false;
	    }
	    
	    if ($.inArray(k.toString(), filteredDocs) !== -1)
		appropriateDoc = false;
	    
	    // add data to object
	    if (appropriateDoc)
		displayedDocs.push(doc);
	}

	// calculate tf-idf of the grammar "query"
	// get df of each (selected) construction (in the settings)
	for (var i in weightSettings_constructions) 
	{
	    var name = weightSettings_constructions[i]["name"];
	    var count = 0; // number of docs with constructions[i] (this construction)
	    for (var j in displayedDocs)
	    {
		if (name.indexOf("LEVEL") > -1)
		{
		    var lev = displayedDocs[j]["readabilityLevel"];
		    if (lev === name)
			count++;
		} 
		else
		{
		    var cs = displayedDocs[j]["constructions"];
		    for (var k in cs)
		    {
			if (cs[k] === name) 
			{
			    if (displayedDocs[j]["frequencies"][k] > 0)
				count++;

			    break;
			}
		    }
		}
	    }
	    // add df (document count) and idf (inverse document frequency) to each construction in the settings
	    weightSettings_constructions[i]["df"] = count;
	    weightSettings_constructions[i]["idf"] = Math.log((displayedDocs.length + 1) / count);
	}

	// calculate average doc length
	for (var d in displayedDocs)
	    avDocLen += displayedDocs[d]["docLength"];

	if (displayedDocs.length > 0)
	    avDocLen = avDocLen / displayedDocs.length;
	else
	    avDocLen = 0;

	//////// - end of tf, idf calculations


	// calculate totalWeight for each doc: BM25
	for (var d in displayedDocs)
	{
	    var dTotal = 0.0;
	    for (var constr in weightSettings_constructions)
	    {
		var name = weightSettings_constructions[constr]["name"];

		if ((weightSettings_constructions[constr]["weight"] > 0 ||
		     weightSettings_constructions[constr]["weight"] < 0) && weightSettings_constructions[constr]["df"] > 0)
		{ 
		    // cannot be NaN
		    var dConstrInd = displayedDocs[d]["constructions"].indexOf(name);
		    // if this construction is in this doc
		    if (dConstrInd > -1 && displayedDocs[d]["frequencies"][dConstrInd] > 0)
		    {
			var tf = displayedDocs[d]["frequencies"][dConstrInd];
			var idf = weightSettings_constructions[constr]["idf"];
			var tfNorm = ((kParam + 1) * tf) / (tf + kParam * (1 - bParam + bParam * (displayedDocs[d]["docLength"] / avDocLen)));
			var gramScore = tfNorm * idf;

			dTotal += gramScore * weightSettings_constructions[constr]["weight"];
		    }
		}
	    }

	    displayedDocs[d]["gramScore"] = dTotal; // grammar score
	    displayedDocs[d]["totalWeight"] = displayedDocs[d]["gramScore"]; // total weight : TODO add rankWeight and textWeight
	}
	//// - end of calculating the total weight

	if (weightSettings_docLevel.length < 4)
	{
	    if (bParam === 0)
	    {
		displayedDocs.sort(function (a, b) {
		    return parseInt(a.preRank) - parseInt(b.preRank);
		});
	    } 
	    else
	    {
		displayedDocs.sort(function (a, b) {
		    return parseInt(a.docLength) - parseInt(b.docLength);
		});
	    }
	}
	else
	{
	    displayedDocs.sort(function (a, b) {
		return Number(b.totalWeight) - Number(a.totalWeight);
	    });
	}

	document.getElementById("docs_info").innerHTML = (displayedDocs.length) + " results";
	for (var s in weightSettings_constructions) 
	{
	    if (weightSettings_constructions[s]["name"].startsWith("LEVEL"))
	    {
		if (document.getElementById(weightSettings_constructions[s]["name"]).checked)
		    document.getElementById(weightSettings_constructions[s]["name"] + "-df").innerHTML = "(" + weightSettings_constructions[s]["df"] + " / " + displayedDocs.length + " results)";
		else
		    document.getElementById(weightSettings_constructions[s]["name"] + "-df").innerHTML = "";
	    } 
	    else
		document.getElementById(weightSettings_constructions[s]["name"] + "-df").innerHTML = "(" + weightSettings_constructions[s]["df"] + " / " + displayedDocs.length + ")";
	}

	var out = "";
	var i;
	for (i = 0; i < displayedDocs.length; i++)
	{
	    // show each object in a row of 3 cells: html / titles, urls and snippets / text
	    out += '<tr><td class="num_cell" style="font-size:x-large;">' +
		    (i + 1) + '&nbsp;<span style="color:lightgrey;font-size:small" title="original position in the rank">(' + displayedDocs[i].preRank + ')</span><br>' +
		    '</td><td  class="url_cell" style="width:40%;"><div><a href="' + displayedDocs[i].url + '" target="_blank"><b>' + displayedDocs[i].title + '</b></a></div>' +
		    '<div id="show_text_cell" title="Click to show text" onclick="FLAIR.WEBRANKER.singleton.displayDetails(' + i + ');"><span style="color:grey;font-size:smaller;">' + displayedDocs[i].urlToDisplay + '</span><br><span>' + displayedDocs[i].snippet + '</span></div></td></tr>';

	}

	document.getElementById("results_table").innerHTML = out;
    };
    
    this.setSearchParams = function(searchQuery, lang, numResults) {
	query = searchQuery;
	language = lang;
	totalResults = numResults;
    };
    this.getQuery = function() {
	return query;
    };
    this.getLanguage = function() {
	return language;
    };
    this.getTotalResults = function() {
	return totalResults;
    };
    this.setTotalResults = function(totalCount) {
	totalResults = totalCount;
    };
    
    this.addParsedDoc = function(doc) {
	parsedDocs.push(doc);
    };
    this.getParsedDocCount = function() {
	return parsedDocs.length;
    };
    
    this.getParsedVisData = function() {
	return parsedVisData;
    };
    this.setParsedVisData = function(csv_string) {
	parsedVisData = csv_string;
    };
    
    this.toggleConstructionExclusion = function(const_element) {
	var slider_id = const_element.parentElement.lastElementChild.firstElementChild.id;
	var construct_name = slider_id.substring(0, slider_id.indexOf("-gradientSlider"));

	// Get the state of the corresponding slider
	var disabled = $("#" + slider_id).slider("option", "disabled");

	// Disable/enable the slider
	$("#" + slider_id).slider("option", "disabled", !disabled);

	// add to/remove from excluded constructions
	if (filteredConstructions.indexOf(construct_name) === -1)
	{
	    // add
	    filteredConstructions.push(construct_name);	    
	}
	else
	{
	    // remove
	    var ind = filteredConstructions.indexOf(construct_name);
	    filteredConstructions.splice(ind, 1);
	}
    };
    this.isConstructionEnabled = function(name) {
	for (var i in weightSettings_docLevel)
	{
	    if (weightSettings_docLevel[i]["name"] === name)
		return true;
	}
	
	return false;
    };
    
    this.addFilteredDoc = function(index) {
	if (filteredDocs.indexOf(index) === -1)
	    filteredDocs.push(index);
    };
    this.clearFilteredDocs = function() {
	filteredDocs = [];
    };
    this.isDocFiltered = function(index) {
	if (filteredDocs.indexOf(index) === -1)
	    return false;
	else
	    return true;
    };
};

FLAIR.WEBRANKER.VISUALISATION = function(delegate_isDocFiltered, delegate_isConstructionEnabled) {
    // PRIVATE VARS
    var margin = {
	top: 55,
	right: 10,
	bottom: 5,
	left: 10
    };
    var width = 800 - margin.left - margin.right;
    var height = 370 - margin.top - margin.bottom;
    
    var x = d3.scale.ordinal().rangePoints([0, width], 1);
    var y = {};
    var dragging = {};

    var line = d3.svg.line();
    var axis = d3.svg.axis().orient("left");
    var background = null;
    var foreground = null;
    var dimensions = null;
    var svg = d3.select("body").select("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	    .append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
    
    var dimension_names = ["document", "# of sentences", "# of words", "readability score"];
    
    var delegates = {
	isDocFiltered: delegate_isDocFiltered,
	isConstructionEnabled: delegate_isConstructionEnabled
    };
    var cache_csvTable = null;	    // the last cached data
    
    // PRIVATE INTERFACE
    var position = function(d) {
	var v = dragging[d];
	return (v === undefined || v === null) ? x(d) : v;
    };
    var transition = function(g) {
	return g.transition().duration(500);
    };
    var path = function(d) {
	// returns the path for a given data point
	return line(dimensions.map(function (p) {
	    return [position(p), y[p](+d[p])];
	}));
    };
    var brushstart = function() {
	d3.event.sourceEvent.stopPropagation();
    };
    var brush = function() {
	// handles a brush event, toggling the display of foreground lines
	var actives = dimensions.filter(function (p) {
	    return !y[p].brush.empty();
	});
	var extents = actives.map(function (p) {
	    return y[p].brush.extent();
	});
	
	foreground.style("display", function (d) {
	    return actives.every(function (p, i) {
		return extents[i][0] <= d[p] && d[p] <= extents[i][1];
	    }) ? null : "none";
	});
    };
    
    // PUBLIC INTERFACE
    this.visualise = function(csv_table) {
	svg.selectAll("*").remove();
	if (csv_table === undefined && cache_csvTable === null)
	    return;
	
	var parsedArray = null;
	if (csv_table === undefined && cache_csvTable !== null)
	    parsedArray = cache_csvTable;
	else
	    parsedArray = cache_csvTable = d3.csv.parse(csv_table);
	
	// extract the list of dimensions and create a scale for each
	x.domain(dimensions = d3.keys(parsedArray[0]).filter(function (d) {
            return d !== "construction" && (delegates.isConstructionEnabled(d) || ($.inArray(d, dimension_names) !== -1))
                    && (y[d] = d3.scale.linear()
                            .domain(d3.extent(parsedArray, function (p) {
                                return +p[d];
                            }))
                            .range([0, height]));
        }));
	
	// add grey background lines for context
        background = svg.append("g")
                .attr("class", "background")
                .selectAll("path")
                .data(parsedArray)
                .enter().append("path")
                .attr("d", path)
                .attr("stroke-width", 2);
	
        // add blue foreground lines for focus
        foreground = svg.append("g")
                .attr("class", "foreground")
                .selectAll("path")
                .data(parsedArray)
                .enter().append("path")
                .attr("d", path)
                .attr("stroke-width", 2);
	
	// add a group element for each dimension
        var g = svg.selectAll(".dimension")
                .data(dimensions)
                .enter().append("g")
                .attr("class", "dimension")
                .attr("transform", function (d) {
                    return "translate(" + x(d) + ")";
                })
                .call(d3.behavior.drag()
                        .origin(function (d) {
                            return {x: x(d)};
                        })
                        .on("dragstart", function (d) {
                            dragging[d] = x(d);
                            background.attr("visibility", "hidden");
                        })
                        .on("drag", function (d) {
                            dragging[d] = Math.min(width, Math.max(0, d3.event.x));
                            foreground.attr("d", path);
                            dimensions.sort(function (a, b) {
                                return position(a) - position(b);
                            });
                            x.domain(dimensions);
                            g.attr("transform", function (d) {
                                return "translate(" + position(d) + ")";
                            });
                        })
                        .on("dragend", function (d) {
                            delete dragging[d];
                            transition(d3.select(this)).attr("transform", "translate(" + x(d) + ")");
                            transition(foreground).attr("d", path);
                            background
                                    .attr("d", path)
                                    .transition()
                                    .delay(500)
                                    .duration(0)
                                    .attr("visibility", null);
                        }));
			
	// add an axis and title
        g.append("g")
                .attr("class", "axis")
                .each(function (d) {
                    d3.select(this).call(axis.scale(y[d]));
                })
                .append("text")
                .style("text-anchor", "middle")
                .style("font-size", "14px")
                .style("font-weight", "bold")
                .attr("y", -25)
                .attr("transform", "rotate(-20)")
                .text(function (d) {
                    // TODO set the text from the interface!
                    if (d === "document") {
                        d = "result";
                    } else if (d === "readability score") {
                        d = "complexity";
                    }

                    var name_to_show = d;
                    var g = document.getElementById(name_to_show + "-df");
                    if (g !== null) {
                        name_to_show = g.parentNode.textContent;
                        if (name_to_show.indexOf("\n") > 0) {
                            name_to_show = name_to_show.substring(0, name_to_show.indexOf("\n"));
                        }
                        if (name_to_show.indexOf("(") > 0) {
                            name_to_show = name_to_show.substring(0, name_to_show.indexOf("("));
                        }
                    }

                    return name_to_show;
                });
	
	// add and store a brush for each axis
        g.append("g")
                .attr("class", "brush")
                .each(function (d) {
                    d3.select(this).call(y[d].brush = d3.svg.brush().y(y[d]).on("brushstart", brushstart).on("brush", brush));
                })
                .selectAll("rect")
                .attr("x", -8)
                .attr("width", 16);
	
	// foreground
        svg.selectAll(".foreground > path")
                .each(function (d) {
                    // do not highlight filtered out docs
                    if (delegates.isDocFiltered(d["document"]))
                        this.style.display = "none";
                });
    };
    this.getFilteredDocs = function() {
	var outArray = [];
	// go through "path" in "svg", select those withOUT style=display:none
	$("path").each(function (d) {
	    if (this.style.display === "none" && delegates.isDocFiltered(this.__data__.document) === false)
		outArray.push(this.__data__.document);
	});
	
	return outArray;
    };
    this.toggleAxis = function(axis_element) {
	var element_id = axis_element.id;
	// axis_name for the 4 default cases: "document", "sentences", "words", "score"
	// for others: e.g., whQuestions, etc.
	var axis_name = element_id.substring(0, element_id.indexOf("-vis"));

	// for default dimensions: TODO
	if (axis_name.indexOf("-def") === axis_name.length - 4)
	{
	    var tmp_name = axis_name.substring(0, axis_name.indexOf("-def"));
	    if (tmp_name === "document")
	    {
		if ($.inArray(tmp_name, dimension_names) === -1)
		    dimension_names.push(tmp_name);
		else
		{
		    // the axis is shown
		    var ind = dimension_names.indexOf(tmp_name);
		    dimension_names.splice(ind, 1);
		}
	    }

	    if (tmp_name === "score")
	    {
		tmp_name = "readability " + tmp_name;
		if ($.inArray(tmp_name, dimension_names) === -1)
		    dimension_names.push(tmp_name);
		else
		{
		    var ind = dimension_names.indexOf(tmp_name);
		    dimension_names.splice(ind, 1);
		}
	    }


	    if (tmp_name === "sentences" || tmp_name === "words")
	    {
		tmp_name = "# of " + tmp_name;
		if ($.inArray(tmp_name, dimension_names) === -1)
		    dimension_names.push(tmp_name);
		else
		{
		    var ind = dimension_names.indexOf(tmp_name);
		    dimension_names.splice(ind, 1);
		}
	    }

	}
	else
	{ 
	    // for constructions:
	    // set the corresponding slider to max
	    // will visualize automatically from rerank() when the value is changed
	    var slider_id = axis_name + "-gradientSlider";
	    // add/remove the axis name to/from the list
	    if ($.inArray(axis_name, dimension_names) === -1)
	    { 
		// the axis is removed
		dimension_names.push(axis_name);
		$("#" + slider_id).slider("value", 5);
	    }
	    else
	    { 
		// the axis is shown
		var ind = dimension_names.indexOf(axis_name);
		dimension_names.splice(ind, 1);
		$("#" + slider_id).slider("value", 0);
	    }
	}
    }; 
    this.resetAxes = function() {
	dimension_names = ["document", "# of sentences", "# of words", "readability score"];
        $("input[id$='-vis']").prop("checked", false);
        $("input[id$='-def-vis']").prop("checked", true);
    };
};

FLAIR.WEBRANKER.INSTANCE = function() {
    // HANDLERS
    var pipeline_noResults = function() {
	FLAIR.WEBRANKER.UTIL.TOGGLE.waitDialog(false);
	FLAIR.WEBRANKER.UTIL.resetUI();
	
	document.getElementById("results_table").innerHTML = "<h4>No results for '" + state.getQuery() + "'.";
	state.reset();
    };
    var pipeline_onError = function() {
	FLAIR.WEBRANKER.UTIL.resetUI();
	state.reset();
	self.deinit();
	
	FLAIR.WEBRANKER.UTIL.TOGGLE.waitDialog(true);
	var status = "<h4>DEAD DOVE. Do Not Eat!</h4>";
	FLAIR.WEBRANKER.UTIL.updateWaitDialog(status, false);
    };
    var complete_webSearch = function(jobID, totalResults) {
	var status = "<h4>Web search complete. Parsing search results now...</h4>";
	FLAIR.WEBRANKER.UTIL.updateWaitDialog(status, true);
	if (totalResults === 0)
	{
	    pipeline_noResults();
	    return;
	}
	
	state.setTotalResults(totalResults);
	if (pipeline.parseSearchResults(jobID) === false)
	    pipeline_onError();
    }; 
    var complete_parseSearchResults = function(jobID, totalDocs) {
	var status = "<h4>Parsing complete. Fetching results now...</h4>";
	FLAIR.WEBRANKER.UTIL.updateWaitDialog(status, false);
	if (totalDocs === 0)
	{
	    pipeline_noResults();
	    return;
	}
	
	state.setTotalResults(totalDocs);
	for (var i = 1; i <= state.getTotalResults(); i++)
	{
	    if (pipeline.fetchParsedData(i, 1) === false)
	    {
		pipeline_onError();
		break;
	    }
	}
    };
    var fetch_searchResults = function(searchResults) {
	// ### nothing to do here as we fetch the parsed data directly
    };
    var fetch_parsedData = function(parsedDocs) {
	for (var i = 0; i < parsedDocs.length; i++)
	    state.addParsedDoc(parsedDocs[i]);
	
	if (state.getParsedDocCount() === state.getTotalResults())
	{
	    var status = "<h4>Ranking results now...</h4>";
	    FLAIR.WEBRANKER.UTIL.updateWaitDialog(status, false);
	    
	    state.rerank();
	    if (pipeline.fetchParsedVisData() === false)
		pipeline_onError();
	}
    };
    var fetch_parsedVisData = function(csvTable) {
	state.setParsedVisData(csvTable);
	visualiser.visualise(csvTable);
	
	FLAIR.WEBRANKER.UTIL.TOGGLE.leftSidebar(true);
	FLAIR.WEBRANKER.UTIL.TOGGLE.rightSidebar(true);
	FLAIR.WEBRANKER.UTIL.TOGGLE.waitDialog(false);
    };
    
    // PRIVATE VARS
    var state = new FLAIR.WEBRANKER.STATE();
    var pipeline = new FLAIR.PLUMBING.PIPELINE(complete_webSearch, complete_parseSearchResults, fetch_searchResults, fetch_parsedData, fetch_parsedVisData);
    var visualiser = new FLAIR.WEBRANKER.VISUALISATION(state.isDocFiltered, state.isConstructionEnabled);
    var initialized = false;
    var self = this;
    
    // PUBLIC INTERFACE
    this.init = function() {
	if (pipeline.init() === false)
	    return;

	initialized = true;	
    };
    this.deinit = function() {
	pipeline.deinit();
	state.reset();
	
	pipeline = null;
	state = null;
	initialized = false;
    };
    
    this.cancelOperation = function() {
	FLAIR.WEBRANKER.UTIL.TOGGLE.waitDialog(false);
	FLAIR.WEBRANKER.UTIL.resetUI();
    
	pipeline.cancelLastJob();  
    };
    this.beginSearch = function() {
	FLAIR.WEBRANKER.UTIL.resetUI();
	
	state.reset();
	state.setSearchParams(document.getElementById("search_field").value.trim(), FLAIR.WEBRANKER.CONSTANTS.DEFAULT_LANGUAGE, FLAIR.WEBRANKER.CONSTANTS.DEFAULT_NUM_RESULTS);

	FLAIR.WEBRANKER.UTIL.TOGGLE.waitDialog(true);
	var status = "<h4>Performing web search now...</h4>";
	FLAIR.WEBRANKER.UTIL.updateWaitDialog(status, true);

	if (pipeline.performSearch(state.getQuery(), state.getLanguage(), state.getTotalResults()) === false)
	    pipeline_onError();
    };
    this.refreshRanking = function() {
	state.rerank();
    };
    this.toggleConstruction = function(const_element) {
	state.toggleConstructionExclusion(const_element);
	
	visualiser.visualise();
	state.rerank();
    };
    this.displayDetails = function(index) {
	state.displayDocText(index);
    };
    this.resetAllSettingsAndFilters = function(sliders, text_characteristics, visualiser_axes, doc_filter) {
	if (sliders === true)
	    FLAIR.WEBRANKER.UTIL.resetSlider("all");
	
	if (text_characteristics === true)
	    FLAIR.WEBRANKER.UTIL.resetTextCharacteristics();
	
	if (doc_filter === true)
	    state.clearFilteredDocs();
	
	if (visualiser_axes === true)
	{
	    visualiser.resetAxes();
	    visualiser.visualise();
	}
	
	state.rerank();
    };
    this.toggleVisualiserAxis = function(axis_element) {
	visualiser.toggleAxis(axis_element);
	visualiser.visualise();
    };
    this.applyVisualiserFilter = function() {
	var docsToFilter = visualiser.getFilteredDocs();
	state.clearFilteredDocs();
	for (var i = 0; i < docsToFilter.length; i++)
	    state.addFilteredDoc(docsToFilter[i]);
	state.rerank();
	FLAIR.WEBRANKER.UTIL.TOGGLE.visualiserDialog(false);
    };
};

FLAIR.WEBRANKER.singleton = new FLAIR.WEBRANKER.INSTANCE();

window.onload = function() {
    $("#menu-toggle").click(function (e) {
	e.preventDefault();
	$("#wrapper").toggleClass("toggled");
    });

    $("#right-menu-toggle").click(function (e) {
	e.preventDefault();
	$("#sidebar-wrapper-right").toggleClass("active");
	$("#page-content-wrapper").toggleClass("active");
    });

    $("#constructs-toggle").click(function (e) {
	e.preventDefault();
	$("#myModal_Constructs").modal('show');
    });

    $('button[data-loading-text]')
	.on('click', function () {
	    var btn = $(this);
	    btn.button('loading');
	    setTimeout(function () {
		btn.button('reset');
	    }, 3000);
    });

    $("[id$=gradientSlider]").slider({
       orientation: "horizontal",
       range: "min",
       max: 5,
       min: 0,
       value: 0
    });
    $("[id$=gradientSlider]").slider("value", 0);
    $("[id$=gradientSlider]").slider({
       change: function (d) {
	// (un)check the corresponding checkbox in the visualization
	var vis_id = this.id;
	vis_id = vis_id.substring(0, vis_id.indexOf("-gradientSlider")) + "-vis";
	var is_checked = $("#" + vis_id).prop("checked");
	$("#" + vis_id).prop("checked", is_checked); // not intuitive but works this way since it is called twice
	
	FLAIR.WEBRANKER.singleton.refreshRanking();
       }
    });

    $(".lengthSlider").slider({
       orientation: "vertical",
       range: "min",
       max: 5,
       min: 0,
       value: 0
    });
    $(".lengthSlider").slider("value", 0);
    $(".lengthSlider").slider({
       change: function (d) {    
	    FLAIR.WEBRANKER.singleton.refreshRanking();
       }
    });

    $(document).ready(function () {
	var theVar = document.getElementById("all_constructions_table");
	if (theVar)
	    $("#all_constructions_table").tablesorter();
    });

    FLAIR.WEBRANKER.UTIL.resetUI();
    FLAIR.WEBRANKER.singleton.init();
};
window.onbeforeunload = function() {
    FLAIR.WEBRANKER.singleton.deinit();
};