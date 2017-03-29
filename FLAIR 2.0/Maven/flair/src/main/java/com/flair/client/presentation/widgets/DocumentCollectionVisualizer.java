package com.flair.client.presentation.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flair.client.localization.LocalizationEngine;
import com.flair.client.localization.LocalizedComposite;
import com.flair.client.localization.SimpleLocalizedTextButtonWidget;
import com.flair.client.localization.SimpleLocalizedTextWidget;
import com.flair.client.localization.locale.DocumentCollectionVisualizerLocale;
import com.flair.client.localization.locale.GrammaticalConstructionLocale;
import com.flair.client.presentation.interfaces.VisualizerService;
import com.flair.client.presentation.widgets.sliderbundles.ConstructionSliderBundleEnglish;
import com.flair.client.presentation.widgets.sliderbundles.ConstructionSliderBundleGerman;
import com.flair.shared.grammar.GrammaticalConstruction;
import com.flair.shared.grammar.Language;
import com.flair.shared.interop.RankableDocument;
import com.github.gwtd3.api.Arrays;
import com.github.gwtd3.api.Coords;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.D3Event;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.behaviour.Drag.DragEventType;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Transition;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.dsv.DsvRow;
import com.github.gwtd3.api.dsv.DsvRows;
import com.github.gwtd3.api.scales.LinearScale;
import com.github.gwtd3.api.scales.OrdinalScale;
import com.github.gwtd3.api.svg.Axis;
import com.github.gwtd3.api.svg.Axis.Orientation;
import com.github.gwtd3.api.svg.Brush;
import com.github.gwtd3.api.svg.Brush.BrushEvent;
import com.github.gwtd3.api.svg.Line;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.animate.MaterialAnimation;

public class DocumentCollectionVisualizer extends LocalizedComposite implements VisualizerService
{
	private static final String			MOCK_TEST_DATA = "Result,existentialThere,thereIsAre,thereWasWere,advancedConjunctions,simpleConjunctions,prepositions,simplePrepositions,complexPrepositions,advancedPrepositions,subordinateClause,relativeClause,relativeClauseReduced,adverbialClause,simpleSentence,complexSentence,compoundSentence,incompleteSentence,directObject,indirectObject,pronouns,pronounsPossessive,pronounsReflexive,pronounsSubjective,someDet,anyDet,muchDet,manyDet,aLotOfDet,articles,theArticle,aArticle,anArticle,negAll,partialNegation,noNotNever,nt,not,directQuestions,indirectQuestions,yesNoQuestions,whQuestions,toBeQuestions,toDoQuestions,toHaveQuestions,modalQuestions,what,who,how,why,where,when,whose,whom,which,tagQuestions,conditionals,condReal,condUnreal,presentSimple,pastSimple,futureSimple,futurePerfect,simpleAspect,progressiveAspect,perfectAspect,perfProgAspect,presentTime,pastTime,futureTime,goingTo,usedTo,toInfinitiveForms,modals,simpleModals,advancedModals,can,must,need,may,could,might,ought,able,haveTo,irregularVerbs,regularVerbs,phrasalVerbs,imperatives,positiveAdj,comparativeAdjShort,superlativeAdjShort,comparativeAdjLong,superlativeAdjLong,positiveAdv,comparativeAdvShort,superlativeAdvShort,comparativeAdvLong,superlativeAdvLong,pluralRegular,pluralIrregular,ingNounForms,presentProgressive,presentPerfProg,pastProgressive,pastPerfProg,futureProgressive,futurePerfProg,shortVerbForms,longVerbForms,auxiliariesBeDoHave,copularVerbs,ingVerbForms,emphaticDo,pronounsPossessiveAbsolute,passiveVoice,presentPerfect,pastPerfect,pronounsObjective,Keywords,# of Sentences,# of Words,Complexity\n1,0.004209720627631076,0,0.0019135093761959434,0.011863758132414848,0.04975124378109453,0.0677382319173364,0.024875621890547265,0.0019135093761959434,0.04286261002678913,0.044393417527745886,0.007654037504783774,0.012246460007654038,0.016073478760045924,0.02525832376578645,0.044393417527745886,0.024492920015308076,0.08381171067738231,0.050133945656333716,0.0003827018752391887,0.09146574818216609,0.01454267125908917,0.005357826253348641,0.05702257941063911,0.002296211251435132,0.0003827018752391887,0.0003827018752391887,0.0003827018752391887,0,0.08725602755453502,0.05702257941063911,0.027554535017221583,0.0026789131266743206,0.01722158438576349,0.0003827018752391887,0.015690776884806735,0.006888633754305396,0.0057405281285878304,0.006505931879066207,0,0.001148105625717566,0.001148105625717566,0.001148105625717566,0.001148105625717566,0,0,0.0003827018752391887,0,0.0007654037504783774,0,0,0,0,0,0,0,0.004209720627631076,0.003827018752391887,0.0003827018752391887,0.028319938767699962,0.06812093379257558,0.001148105625717566,0,0.09758897818599312,0.005357826253348641,0.004975124378109453,0,0.030233448143895905,0.07654037504783773,0.001148105625717566,0.0003827018752391887,0,0.01990049751243781,0.018369690011481057,0.0030616150019135095,0.013777267508610792,0.0015308075009567547,0.001148105625717566,0,0.0003827018752391887,0.003444316877152698,0.0015308075009567547,0.0007654037504783774,0,0.0003827018752391887,0.0776884806735553,0.028319938767699962,0.009950248756218905,0.005357826253348641,0.04630692690394183,0.0015308075009567547,0.0007654037504783774,0,0.001148105625717566,0.09031764255644853,0.002296211251435132,0.001148105625717566,0,0,0.017604286261002678,0.002296211251435132,0.001148105625717566,0,0,0.005357826253348641,0,0,0,0.012246460007654038,0.011481056257175661,0.021814006888633754,0.01722158438576349,0.028319938767699962,0,0,0.004975124378109453,0.0019135093761959434,0.0030616150019135095,0.01454267125908917,0.001148105625717566,465,2613,6\n2,0.003480753480753481,0,0.0018427518427518428,0.01126126126126126,0.04954954954954955,0.07166257166257166,0.02108927108927109,0.003276003276003276,0.050573300573300575,0.03644553644553645,0.006347256347256347,0.014127764127764128,0.015356265356265357,0.02231777231777232,0.03644553644553645,0.033988533988533985,0.07800982800982802,0.057944307944307945,0.0014332514332514332,0.09561834561834562,0.019656019656019656,0.006552006552006552,0.05016380016380016,0.0010237510237510238,0.0014332514332514332,0,0.0004095004095004095,0,0.07534807534807535,0.04606879606879607,0.02661752661752662,0.0026617526617526617,0.018427518427518427,0.0012285012285012285,0.016994266994266993,0.0085995085995086,0.0036855036855036856,0.009009009009009009,0,0.0010237510237510238,0.0020475020475020475,0.0018427518427518428,0.0010237510237510238,0,0.00020475020475020476,0.0006142506142506142,0.000819000819000819,0.0004095004095004095,0,0,0,0,0,0.00020475020475020476,0,0.001638001638001638,0.0014332514332514332,0.00020475020475020476,0.03235053235053235,0.06306306306306306,0.0012285012285012285,0,0.09664209664209664,0.0028665028665028664,0.006142506142506142,0.00020475020475020476,0.0343980343980344,0.07022932022932023,0.0012285012285012285,0.00020475020475020476,0.00020475020475020476,0.017813267813267815,0.020065520065520065,0.0045045045045045045,0.013104013104013105,0.003071253071253071,0.0012285012285012285,0,0.00020475020475020476,0.0018427518427518428,0.0010237510237510238,0.000819000819000819,0,0.000819000819000819,0.07145782145782145,0.028255528255528257,0.009623259623259623,0.006961506961506962,0.053644553644553644,0.0036855036855036856,0.001638001638001638,0.00020475020475020476,0,0.0819000819000819,0.0020475020475020475,0.00020475020475020476,0.0004095004095004095,0.00020475020475020476,0.02293202293202293,0.0012285012285012285,0.0022522522522522522,0,0,0.0028665028665028664,0.00020475020475020476,0,0,0.012285012285012284,0.020065520065520065,0.01760851760851761,0.0171990171990172,0.022113022113022112,0.0006142506142506142,0.00020475020475020476,0.0045045045045045045,0.0020475020475020475,0.004095004095004095,0.019041769041769043,0.0042997542997543,834,4884,6";
	
	private static final Margin			MARGINS = new Margin(55, 5, 10, 10);
	private static final int			WIDTH = 990;
	private static final int			HEIGHT = 600;
	
	enum DefaultDimension
	{
		RESULT,
		NUM_WORDS,
		NUM_SENTENCES,
		COMPLEXITY,
		KEYWORDS,
	}
	
	final class State
	{
		Input							input;
		int								width;
		int								height;
		OrdinalScale					x;
		Map<String, LinearScale>		y;				// axis -> scale
		Map<String, Brush>				brushes;		// axis -> brush
		Map<String, Coords>				dragCoords;		// axis -> X coord
		Line							line;
		Axis							axis;
		Selection						background;
		Selection						foreground;
		Array<String>					dimensions;		// as read in from the CSV header, used as y axes
		Selection						svg;
		Selection						group;
		DsvRows<DsvRow>					cache;			// the parsed CSV string
		Set<String>						selectedAxes;	// axes displayed in the visualization
		
		LanguageSpecificConstructionSliderBundle	toggles;
		VisualizerService.ApplyFilterHandler		filterHandler;
		VisualizerService.ResetFilterHandler		resetHandler;
		
		RankableDocument getDoc(int rank)
		{
			for (RankableDocument itr : input.getDocuments())
			{
				if (itr.getRank() == rank)
					return itr;
			}
			
			return null;
		}
		
		void toggleAxis(String dim, boolean state, boolean reload)
		{
			if (state)
				selectedAxes.add(dim);
			else
				selectedAxes.remove(dim);
			
			if (reload)
				reload(false);
		}
		
		String getConstructionDimesionId(GrammaticalConstruction gram) {
			return gram.getID();
		}
		
		String getDefaultDimensionId(DefaultDimension dim)
		{
			switch (dim)
			{
			case COMPLEXITY:
				return getLocalizedString(DocumentCollectionVisualizerLocale.DESC_axisComplexity);
			case NUM_SENTENCES:
				return getLocalizedString(DocumentCollectionVisualizerLocale.DESC_axisSentences);
			case NUM_WORDS:
				return getLocalizedString(DocumentCollectionVisualizerLocale.DESC_axisWords);
			case RESULT:
				return getLocalizedString(DocumentCollectionVisualizerLocale.DESC_axisResult);
			case KEYWORDS:
				return getLocalizedString(DocumentCollectionVisualizerLocale.DESC_axisKeywords);
			default:
				return "";
			}
		}
		
		String generateFrequencyTable()
		{
			// construct CSV string
			StringBuilder writer = new StringBuilder();
			
			// the header first
			// the dimensions need to be mapped to unique IDs/names as we need to look them up later
			// we can use localized names as long as the interface language doesn't change (and they don't contain commas)
			writer.append(getDefaultDimensionId(DefaultDimension.RESULT)).append(",");
			for (GrammaticalConstruction itr : input.getConstructions())
					writer.append(getConstructionDimesionId(itr)).append(",");
			
			writer.append(getDefaultDimensionId(DefaultDimension.KEYWORDS))
				.append(",")
				.append(getDefaultDimensionId(DefaultDimension.NUM_SENTENCES))
				.append(",")
				.append(getDefaultDimensionId(DefaultDimension.NUM_WORDS))
				.append(",")
				.append(getDefaultDimensionId(DefaultDimension.COMPLEXITY))
				.append("\n");

			// the rest comes next
			for (RankableDocument itr : input.getDocuments())
			{
				int id = itr.getRank();
				writer.append("" + id + ",");
				for (GrammaticalConstruction gramConst : input.getConstructions())
				{
					Double relFreq = itr.getConstructionRelFreq(gramConst);
					writer.append(relFreq.toString() + ",");
				}

				double keywordRelFreq = itr.getKeywordRelFreq();
				writer.append("" + keywordRelFreq + "," + itr.getNumSentences() + "," + itr.getNumWords() + ","
						+ itr.getReadablilityScore());
				
				writer.append("\n");
			}

			return writer.toString();
		}
		
		int getXPosition(String dim)
		{
			int out = 0;
			if (dragCoords.containsKey(dim))
				out = (int)dragCoords.get(dim).x();
			else
				out = x.apply(dim).asInt();
			
			return out;
		}
		
		Transition getTransition(Selection in) {
			return in.transition().duration(500);
		}
		
		String getPathString(Value val)
		{
			// returns the path for a given data point
			Array<?> out = dimensions.map((t, e, i, a) -> {
				String dim = e.asString();
				return Array.fromDoubles(getXPosition(dim),
							y.get(dim).apply(+val.getProperty(dim).asDouble()).asDouble());
			});
			
			String path = line.generate(out);
			return path;
		}
		
		void onBrushStart()
		{
			D3Event d3Event = D3.event();
			d3Event.sourceEvent().stopPropagation();
		}
		
		void onBrush()
		{
			// handles a brush event, toggling the display of foreground lines
			Array<String> actives = dimensions.filter((t, e, i, a) -> {
				String dim = e.asString();
				return !brushes.get(dim).empty();
			});
			
			Array<Array<Double>> extents = actives.map((t,e,i,a) -> {
				String dim = e.asString();
				return brushes.get(dim).extent();
			});
			
			foreground.style("display", (c,d,x) -> {
				return actives.every((t,e,i,a) -> {
					String dim = e.asString();
					double e1 = d.getProperty(dim).asDouble();
					return extents.get(i).getNumber(0) <= e1 && e1 <= extents.get(i).getNumber(1);
				}) ? "initial" : "none";
			});
		}
		
		void reload(boolean full)
		{
			// clear up previous state
			if (svg != null)
				svg.selectAll("*").remove();
			else
			{
				svg = D3.select(pnlSVGContainerUI.getElement())
						.append("svg")
						.attr("width", width + MARGINS.left + MARGINS.right)
						.attr("height", height + MARGINS.top + MARGINS.bottom)
						.append("g")
						.attr("transform", "translate(" + MARGINS.left + "," + MARGINS.top + ")");
			}
			
			group = null;
			brushes.clear();
			y.clear();
			brushes.clear();
			dragCoords.clear();
			
			// generate CSV data from the input and reset cache if necessary
			if (full)
				cache = D3.csv().parse(generateFrequencyTable());
			
			// extract the list of dimensions and create a scale for each
			dimensions = D3.keys(cache.get(0)).filter((t, e, i, a) -> {
				String dim = e.asString();
				
				// skip if the dimension isn't selected
				if (selectedAxes.contains(dim) == false)
					return false;
				
				// add scale
				JsArrayMixed extents = Arrays.extent(cache, (d, idx) -> {
					DsvRow row = (DsvRow)d;
					return +row.get(dim).asDouble();
				});
				
				y.put(dim, D3.scale.linear()
								.domain(extents)
								.range(0, height));
				
				return true;
			});
			
			x.domain(dimensions);
			
			// add grey background lines for context
			background = svg.append("g")
							.attr("class", "background")
							.selectAll("path")
							.data(cache)
							.enter()
							.append("path")
							.attr("d", (c,d,i) -> getPathString(d))
							.attr("stroke-width", 2);
			
			// add blue foreground lines for focus
			foreground = svg.append("g")
							.attr("class", "foreground")
							.selectAll("path")
							.data(cache)
							.enter()
							.append("path")
							.attr("d", (c,d,i) -> getPathString(d))
							.attr("stroke-width", 2);
			
			// add a group element for each dimension
			group = svg.selectAll(".dimension")
							  .data(dimensions)
							  .enter()
							  .append("g")
							  .attr("class", "dimension")
							  .attr("transform", (c,d,i) -> {
								  int pos = getXPosition(d.asString());
								  return "translate(" + pos + ")";
							  })
							  .call(D3.behavior().drag()
									  			 .origin((c,d,i) -> {
									  				 int startX = getXPosition(d.asString());
									  				 return Coords.create(startX, 0);
									  			 })
									  			 .on(DragEventType.DRAGSTART, (c,d,i) -> {
									  				String dim = d.asString();
									  				
									  				int startX = getXPosition(dim);
									  				dragCoords.put(dim, Coords.create(startX, 0));
									  				background.attr("visibility", "hidden");
									  				return null;
									  			 })
									  			 .on(DragEventType.DRAG, (c,d,i) -> {
									  				String dim = d.asString();
									  				Coords coords = dragCoords.get(dim);
									  				
									  				// update x coords and sort dimension based on position
									  				D3Event d3Event = D3.event();
									  				int delta = (int) D3.eventAsDCoords().x();
									  				int oldX = (int) coords.x();
									  				int newX = Math.min(width, Math.max(0, oldX + delta));
									  				coords.x(newX);
									  				
									  				foreground.attr("d", (c1,d1,i1) -> getPathString(d1));
									  				// sort the dimensions by position along the x-axis
									  				// workaround for d3gwt's incomplete ArrayList API
									  				List<String> buffer = new ArrayList<>();
									  				for (Value itr : dimensions.asIterable())
									  					buffer.add(itr.asString());
									  					
									  				Collections.sort(buffer, (a, b) -> {
									  					return getXPosition(a) - getXPosition(b);
									  				});
									  				
									  				dimensions = Array.fromIterable(buffer);
									  				x.domain(dimensions);
									  				
									  				group.attr("transform", (c1,d1,i1) -> {
									  					return "translate(" + getXPosition(d1.asString()) + ")";
									  				});
									  				
									  				return null;
									  			 })
									  			 .on(DragEventType.DRAGEND, (c,d,i) -> {
									  				String dim = d.asString();
									  				
									  				dragCoords.remove(dim);
									  				
									  				getTransition(D3.select(c)).attr("transform", "translate(" + x.apply(dim).asInt() + ")");
									  				getTransition(foreground).attr("d", (c1,d1,i1) -> getPathString(d1));
									  				background.attr("d", (c1,d1,i1) -> getPathString(d1))
									  						  .transition()
									  						  .delay(100)
									  						  .duration(100)
									  						  .attr("visibility", "visible");
									  				
									  				return null;
									  			 }));
			
			// add axis and titles
			group.append("g")
				 .attr("class", "axis")
				 .each((c,d,i) -> {
					 D3.select(c).call(axis.scale(y.get(d.asString())));
					 return null;
				 })
				 .append("text")
				 .style("text-anchor", "middle")
				 .style("font-size", "14px")
				 .style("font-weight", "bold")
				 .attr("y", -25)
				 .attr("transform", "rotate(-20)")
				 .text((c,d,i) -> {
					 GrammaticalConstruction gram = GrammaticalConstruction.lookup(d.asString());
					 if (gram != null)
					 {
						 // get the localized string and format it a bit
						 String name = GrammaticalConstructionLocale.get().getLocalizedName(gram, localeCore.getLanguage());
						 int delimiter = name.indexOf("(");
						 if (delimiter == -1)
							 return name;
						 else
							 return name.substring(0, delimiter);
					 }
					 else
						 return d.asString();	// for the default dimensions, use the same text as it's already localized
				 });
			
			// store brushes for each axis
			group.append("g")
				 .attr("class", "brush")
				 .each((c,d,i) -> {
					 String dim = d.asString();
					 Brush brush = D3.svg().brush()
							 			   .y(y.get(dim))
							 			   .on(BrushEvent.BRUSH_START, (cx,dx,ix) -> {
							 				  onBrushStart();
							 				  return null;
							 			   })
							 			   .on(BrushEvent.BRUSH, (cx,dx,ix) -> {
							 				  onBrush();
							 				  return null;
							 			   });
					 
					 brushes.put(dim, brush);
					 D3.select(c).call(brush);
					 return null;
				 })
				 .selectAll("rect")
				 .attr("x", -8)
				 .attr("width", 16);
		}
		
		List<RankableDocument> getFilteredDocs()
		{
			List<RankableDocument> out = new ArrayList<>();
			// go through "path" in "svg", select those withOUT style=display:none
			svg.selectAll("path")
			  .each((c,d,i) -> {
				  if (c.getStyle().getDisplay() == Display.NONE.getCssName())
				  {
					  String resultDim = getDefaultDimensionId(DefaultDimension.RESULT);
					  int rank = d.getProperty(resultDim).asInt();
					  out.add(getDoc(rank));
				  }
				  
				  return null;
			  });
			
			return out;
		}
		
		void resetSelectedAxes()
		{
			selectedAxes.clear();
			
			// add the default dimensions
			toggleDefaultAxis(DefaultDimension.RESULT, true, false);
			toggleDefaultAxis(DefaultDimension.NUM_WORDS, true, false);
		}
		
		void doInit(Input i)
		{
			input = i;
			
			chkAxisWordsUI.setValue(true, false);
			chkAxisSentencesUI.setValue(false, false);
			chkAxisComplexityUI.setValue(false, false);
			chkAxisKeywordsUI.setValue(false, false);
			
			bdlEnglishSlidersUI.setVisible(false);
			bdlGermanSlidersUI.setVisible(false);
			
			// setup sliders
			switch (input.getSliders().getLanguage())
			{
			case ENGLISH:
				toggles = bdlEnglishSlidersUI;
				break;
			case GERMAN:
				toggles = bdlGermanSlidersUI;
				break;
			}
			
			toggles.setVisible(true);
			toggles.forEachWeightSlider(n -> {
				GrammaticalConstructionWeightSlider o = input.getSliders().getWeightSlider(n.getGram());
				n.setEnabled(false, false);
				n.setSliderVisible(false);
				n.setResultCountVisible(false);
				n.refreshLocalization();
				n.setWeight(o.getWeight(), false);
				
				// toggle axis if the slider is weighted
				if (o.hasWeight())
				{
					String dim = getConstructionDimesionId(n.getGram());
					toggleAxis(dim, true, false);
					n.setEnabled(true, false);
				}
				
				n.setToggleHandler((w, e) -> {
					GrammaticalConstructionWeightSlider slider = (GrammaticalConstructionWeightSlider)w;
					String dim = getConstructionDimesionId(slider.getGram());
					toggleAxis(dim, e, true);
					
					// set/reset the weights of the original sliders
					GrammaticalConstructionWeightSlider old = input.getSliders().getWeightSlider(slider.getGram());
					if (e)
						old.setWeight(slider.getSliderMax(), true);
					else
						old.setWeight(slider.getSliderMin(), true);
				});
				
				n.setResetHandler((w,e) -> {
					// turn off axis
					w.setEnabled(false, e);
				});
			});
			
			resetSelectedAxes();
		}
		
		State()
		{
			input = null;
			
			width = WIDTH - MARGINS.left - MARGINS.right;
			height = HEIGHT - MARGINS.top - MARGINS.bottom;
			
			x = D3.scale.ordinal().rangePoints(0, width, 1);
			y = new HashMap<>();
			brushes = new HashMap<>();
			dragCoords = new HashMap<>();
			line = D3.svg().line();
			axis = D3.svg().axis().orient(Orientation.LEFT);
			background = foreground = null;
			dimensions = null;
			
			svg = null;
			group = null;
			cache = null;
			selectedAxes = new HashSet<>();
			toggles = null;
			filterHandler = null;
			resetHandler = null;
		}
		
		public void init(Input i)
		{
			doInit(i);
			reload(true);
		}
		
		public void resetFilter()
		{
			chkAxisWordsUI.setValue(true, false);
			chkAxisSentencesUI.setValue(false, false);
			chkAxisComplexityUI.setValue(false, false);
			chkAxisKeywordsUI.setValue(false, false);
			
			toggles.resetState(false);
			resetSelectedAxes();
			reload(false);
			
			if (resetHandler != null)
				resetHandler.handle();
		}
		
		public void applyFilter()
		{
			List<RankableDocument> filtered = getFilteredDocs();
			
			if (filtered.isEmpty())
				MaterialToast.fireToast(getLocalizedString(DocumentCollectionVisualizerLocale.DESC_NoFilteredDocs));
			else
			{
				String msg = getLocalizedString(DocumentCollectionVisualizerLocale.DESC_HasFilteredDocs	) + ": " + filtered.size();
				MaterialToast.fireToast(msg);
			}
			
			if (filterHandler != null)
				filterHandler.handle(filtered);
		}
		
		public void toggleDefaultAxis(DefaultDimension dim, boolean state, boolean reload)
		{
			String name = getDefaultDimensionId(dim);
			toggleAxis(name, state, reload);
		}
		
		public void setFilterHandler(ApplyFilterHandler handler) {
			filterHandler = handler;
		}
		
		public void setResetHandler(ResetFilterHandler handler) {
			resetHandler = handler;
		}
	}
	
	
	private static DocumentCollectionVisualizerUiBinder uiBinder = GWT
			.create(DocumentCollectionVisualizerUiBinder.class);

	interface DocumentCollectionVisualizerUiBinder extends UiBinder<Widget, DocumentCollectionVisualizer>
	{
	}

	@UiField
	MaterialWindow				mdlVisualizerUI;
	@UiField
	MaterialLabel				lblTitleUI;
	@UiField
	FlowPanel					pnlSVGContainerUI;
	@UiField
	MaterialButton				btnResetUI;
	@UiField
	MaterialCheckBox			chkAxisWordsUI;
	@UiField
	MaterialCheckBox			chkAxisSentencesUI;
	@UiField
	MaterialCheckBox			chkAxisComplexityUI;
	@UiField
	MaterialCheckBox			chkAxisKeywordsUI;
	@UiField
	MaterialRow					pnlToggleContainerUI;
	@UiField
	ConstructionSliderBundleEnglish		bdlEnglishSlidersUI;
	@UiField
	ConstructionSliderBundleGerman		bdlGermanSlidersUI;
	@UiField
	MaterialButton				btnApplyUI;
	
	SimpleLocalizedTextWidget<MaterialLabel>			lblTitleLC;
	SimpleLocalizedTextButtonWidget<MaterialButton>		btnResetLC;
	SimpleLocalizedTextWidget<MaterialCheckBox>			chkAxisWordsLC;
	SimpleLocalizedTextWidget<MaterialCheckBox>			chkAxisSentencesLC;
	SimpleLocalizedTextWidget<MaterialCheckBox>			chkAxisComplexityLC;
	SimpleLocalizedTextWidget<MaterialCheckBox>			chkAxisKeywordsLC;
	SimpleLocalizedTextButtonWidget<MaterialButton>		btnApplyLC;
	
	State	state;
	
	private void initLocale()
	{
		lblTitleLC = new SimpleLocalizedTextWidget<>(lblTitleUI, DocumentCollectionVisualizerLocale.DESC_lblTitleCaptionUI);
		btnResetLC = new SimpleLocalizedTextButtonWidget<>(btnResetUI, DocumentCollectionVisualizerLocale.DESC_btnResetUI);
		chkAxisWordsLC = new SimpleLocalizedTextWidget<>(chkAxisWordsUI, DocumentCollectionVisualizerLocale.DESC_chkAxisWordsUI);
		chkAxisSentencesLC = new SimpleLocalizedTextWidget<>(chkAxisSentencesUI, DocumentCollectionVisualizerLocale.DESC_chkAxisSentencesUI);
		chkAxisComplexityLC = new SimpleLocalizedTextWidget<>(chkAxisComplexityUI, DocumentCollectionVisualizerLocale.DESC_chkAxisComplexityUI);
		chkAxisKeywordsLC = new SimpleLocalizedTextWidget<>(chkAxisKeywordsUI, DocumentCollectionVisualizerLocale.DESC_chkAxisKeywordsUI);
		btnApplyLC = new SimpleLocalizedTextButtonWidget<>(btnApplyUI, DocumentCollectionVisualizerLocale.DESC_btnApplyUI);

		registerLocale(DocumentCollectionVisualizerLocale.INSTANCE.en);
		registerLocale(DocumentCollectionVisualizerLocale.INSTANCE.de);
		
		registerLocalizedWidget(lblTitleLC);
		registerLocalizedWidget(btnResetLC);
		registerLocalizedWidget(chkAxisWordsLC);
		registerLocalizedWidget(chkAxisSentencesLC);
		registerLocalizedWidget(chkAxisComplexityLC);
		registerLocalizedWidget(chkAxisKeywordsLC);
		registerLocalizedWidget(btnApplyLC);
	}
	
	private void initHandlers()
	{
		btnResetUI.addClickHandler(e -> state.resetFilter());
		
		chkAxisWordsUI.addValueChangeHandler(e -> {
			state.toggleDefaultAxis(DefaultDimension.NUM_WORDS, e.getValue(), true);
		});
		chkAxisSentencesUI.addValueChangeHandler(e -> {
			state.toggleDefaultAxis(DefaultDimension.NUM_SENTENCES, e.getValue(), true);
		});
		chkAxisComplexityUI.addValueChangeHandler(e -> {
			state.toggleDefaultAxis(DefaultDimension.COMPLEXITY, e.getValue(), true);
		});
		chkAxisKeywordsUI.addValueChangeHandler(e -> {
			state.toggleDefaultAxis(DefaultDimension.KEYWORDS, e.getValue(), true);
		});
		
		btnApplyUI.addClickHandler(e -> {
			state.applyFilter();
			hide();
		});
	}
	
	private void initUI()
	{
		MaterialAnimation open = new MaterialAnimation();
		open.setTransition(gwt.material.design.client.ui.animate.Transition.FADEINDOWN);
		open.setDelayMillis(0);
		open.setDurationMillis(650);
		mdlVisualizerUI.setOpenAnimation(open);
		
		MaterialAnimation close = new MaterialAnimation();
		close.setTransition(gwt.material.design.client.ui.animate.Transition.FADEOUTUP);
		close.setDelayMillis(0);
		close.setDurationMillis(650);
		mdlVisualizerUI.setCloseAnimation(close);
		
		// hide the maximize button
		mdlVisualizerUI.getIconMaximize().setVisible(false);
	}
	
	public DocumentCollectionVisualizer()
	{
		super(LocalizationEngine.get());
		initWidget(uiBinder.createAndBindUi(this));
		
		state = new State();
		
		initLocale();
		initHandlers();
		initUI();
	}
	
	@Override
	public void setLocalization(Language lang)
	{
		super.setLocalization(lang);
		
		mdlVisualizerUI.setTitle(getLocalizedString(DocumentCollectionVisualizerLocale.DESC_lblTitleUI));
	}

	@Override
	public void show()
	{
		MaterialWindow.setOverlay(true);
		mdlVisualizerUI.open();
	}

	@Override
	public void hide() {
		mdlVisualizerUI.close();
	}

	@Override
	public void visualize(Input input) {
		state.init(input);
	}

	@Override
	public void setApplyFilterHandler(ApplyFilterHandler handler) {
		state.setFilterHandler(handler);
	}

	@Override
	public void setResetFilterHandler(ResetFilterHandler handler) {
		state.setResetHandler(handler);
	}

}

final class Margin
{
	int		top;
	int		bottom;
	int		left;
	int		right;
	
	Margin(int t, int b, int l, int r)
	{
		top = t;
		bottom = b;
		left = l;
		right = r;
	}
}