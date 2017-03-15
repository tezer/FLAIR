package com.flair.client.presentation.widgets;

import java.util.List;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelFooter;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.client.ui.html.Small;
import org.gwtbootstrap3.client.ui.html.Strong;

import com.flair.client.localization.LocalizationData;
import com.flair.client.localization.LocalizedComposite;
import com.flair.client.localization.SimpleLocalizedTextWidget;
import com.flair.client.localization.SimpleLocalizedWidget;
import com.flair.client.localization.locale.DocumentPreviewPaneLocale;
import com.flair.client.localization.locale.GrammaticalConstructionLocale;
import com.flair.client.localization.locale.KeywordWeightSliderLocale;
import com.flair.client.model.ClientEndPoint;
import com.flair.client.presentation.interfaces.AbstractDocumentPreviewPane;
import com.flair.client.presentation.interfaces.DocumentPreviewPaneInput;
import com.flair.client.presentation.interfaces.DocumentPreviewPaneInput.UnRankable;
import com.flair.client.presentation.interfaces.DocumentPreviewPaneInput.Rankable;
import com.flair.shared.grammar.GrammaticalConstruction;
import com.flair.shared.grammar.Language;
import com.flair.shared.parser.RankableDocument;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DocumentPreviewPane extends LocalizedComposite implements AbstractDocumentPreviewPane
{	
	private static DocumentPreviewPaneUiBinder uiBinder = GWT.create(DocumentPreviewPaneUiBinder.class);

	interface DocumentPreviewPaneUiBinder extends UiBinder<Widget, DocumentPreviewPane>
	{
	}

	@UiField
	FlowPanel					pnlPlaceholderUI;
	@UiField
	InlineLabel					lblPlaceholderTextUI;
	@UiField
	Container					pnlPreviewContainerUI;
	@UiField
	Strong						lblDocTitleUI;
	@UiField
	Strong						lblDocLevelUI;
	@UiField
	Small						lblDocNumSentencesUI;
	@UiField
	Small						lblDocNumWordsUI;
	@UiField
	FlowPanel					pnlWeightSelectionUI;
	@UiField
	Icon						icoHelpTextUI;
	@UiField
	HTML						htmlDocTextPreviewUI;
	@UiField
	PanelFooter					pnlFooterUI;
	@UiField
	Anchor						tglConstructionDetailsUI;
	@UiField
	PanelBody					pnlConstructionDetailsUI;
	
	SimpleLocalizedTextWidget<InlineLabel>		lblPlaceholderTextLC;
	SimpleLocalizedWidget<Icon>					icoHelpTextLC;
	SimpleLocalizedTextWidget<Anchor>			tglConstructionDetailsLC;
	
	State						state;
	
	private enum TableType
	{
		WEIGHT_SELECTION,
		ALL_CONSTRUCTIONS
	}
	
	private enum InputType
	{
		RANKABLE,
		UNRANKABLE
	}
	
	private static final class TableData
	{
		private final GrammaticalConstruction		gram;
		private final boolean						keyword;
		private final boolean						customKeyword;
		private final int							hits;
		private final double						weight;
		private final double						relFreq;
		
		TableData(GrammaticalConstruction gram, int hits, double weight, double relFreq)
		{
			this.gram = gram;
			this.hits = hits;
			this.weight = weight;
			this.relFreq = relFreq;
			
			keyword = customKeyword = false;
		}
		
		TableData(boolean customKeyword, int hits, double weight, double relFreq)
		{
			this.gram = null;
			this.hits = hits;
			this.weight = weight;
			this.relFreq = relFreq;
			
			this.keyword = true;
			this.customKeyword = customKeyword;
		}
		
		public String getLocalizedName(Language lang)
		{
			if (keyword)
				return KeywordWeightSliderLocale.INSTANCE.getLocalizedKeywordString(lang, customKeyword);
			else
				return GrammaticalConstructionLocale.get().getLocalizedPath(gram, lang);
		}
	}
	
	
	private class State
	{		
		class Table
		{
			private static final int		PAGE_SIZE = 200;		// > no. of gram consts to disable pagination
			
			private static final String		WEIGHT_SEL_HEIGHT = "150px";
			private static final String		ALL_CONST_HEIGHT = "350px";
			
			TableType						type;
			CellTable<TableData>			table;
			ListDataProvider<TableData>		dataProvider;
			Column<TableData,?>				colFirst;
			TextColumn<TableData>			colSecond;
			TextColumn<TableData>			colThird;
			
			Table(TableType type, ListDataProvider<TableData> data)
			{
				this.type = type;
				table = new CellTable<>(PAGE_SIZE);
				dataProvider = data;
				if (dataProvider == null)
					dataProvider = new ListDataProvider<>();
				dataProvider.addDataDisplay(table);
				
				ListHandler<TableData> sortHandler = new ListHandler<TableData>(dataProvider.getList());
				
				switch (type)
				{
				case WEIGHT_SELECTION:
					table.setHeight(WEIGHT_SEL_HEIGHT);
					
					colFirst = new Column<TableData, SafeHtml>(new SafeHtmlCell()) {
						@Override
						public SafeHtml getValue(TableData item) 
						{
							String text = item.getLocalizedName(localeCore.getLanguage());
							String color = item.keyword ? rankable.getKeywordAnnotationColor() : rankable.getConstructionAnnotationColor(item.gram);
							SafeHtmlBuilder sb = new SafeHtmlBuilder();
							
							sb.appendHtmlConstant("<span style='background-color:")
							  .appendHtmlConstant(color)
							  .appendHtmlConstant(";'>")
							  .appendHtmlConstant(text)
							  .appendHtmlConstant("</span>");
							
							return sb.toSafeHtml();
						}
					};
					
					colThird = new TextColumn<TableData>() {
						@Override
						public String getValue(TableData item) {
							return "(" + item.weight + ")";
						}
					};
					sortHandler.setComparator(colThird, (a, b) -> {
						return Double.compare(a.weight, b.weight);
					});
					
					break;
				case ALL_CONSTRUCTIONS:
					table.setHeight(ALL_CONST_HEIGHT);
					
					colFirst = new TextColumn<TableData>() {
						@Override
						public String getValue(TableData item) {
							return item.getLocalizedName(localeCore.getLanguage());
						}
					};
					
					colThird = new TextColumn<TableData>() {
						@Override
						public String getValue(TableData item) {
							return "(" + item.relFreq + ")";
						}
					};
					sortHandler.setComparator(colThird, (a, b) -> {
						return Double.compare(a.relFreq, b.relFreq);
					});
					
					break;
				}
				
				colFirst.setSortable(true);
				sortHandler.setComparator(colFirst, (a, b) -> {
					return a.getLocalizedName(localeCore.getLanguage()).compareToIgnoreCase(b.getLocalizedName(localeCore.getLanguage()));
				});
				
				colSecond = new TextColumn<TableData>() {
					@Override
					public String getValue(TableData item) {
						return ((Integer)item.hits).toString();
					}
				};
				colSecond.setSortable(true);
				sortHandler.setComparator(colSecond, (a, b) -> {
					return Integer.compare(a.hits, b.hits);
				});
				
				colThird.setSortable(true);
				table.addColumnSortHandler(sortHandler);
			}
			
			public void initColumns(String col1, String col2, String col3)
			{
				table.addColumn(colFirst, col1);
				table.addColumn(colSecond, col2);
				table.addColumn(colThird, col3);
			}
		}
		
		InputType								type;
		DocumentPreviewPaneInput.Rankable 		rankable;
		DocumentPreviewPaneInput.UnRankable 	unrankable;
		Table									weightSelection;
		Table 									constructionDetails;
		
		State()
		{
			type = null;
			rankable = null;
			unrankable = null;
			weightSelection = null;
			constructionDetails = null;
		}
		
		private void genTableData(List<TableData> ws, List<TableData> cd)
		{			
			ws.clear();
			cd.clear();
			
			// update data
			if (rankable.shouldShowKeywords())
			{
				ws.add(new TableData(rankable.hasCustomKeywords(),
						(int)rankable.getDocument().getKeywordCount(),
						rankable.getKeywordWeight(), 0));
			}
			
			for (GrammaticalConstruction itr : GrammaticalConstruction.values())
			{
				TableData d = new TableData(itr,
						(int)rankable.getDocument().getConstructionFreq(itr),
						rankable.getConstructionWeight(itr),
						rankable.getDocument().getConstructionRelFreq(itr));
				
				cd.add(d);
				if (rankable.isConstructionWeighted(itr))
					ws.add(d);
			}
			
			weightSelection.dataProvider.refresh();
			constructionDetails.dataProvider.refresh();
		}
		
		public void init(DocumentPreviewPaneInput.Rankable input)
		{
			type = InputType.RANKABLE;
			rankable = input;
			unrankable = null;
					
			reload(true);
		}
		
		public void init(DocumentPreviewPaneInput.UnRankable input)
		{
			type = InputType.UNRANKABLE;
			rankable = null;
			unrankable = input;
					
			reload(true);
		}
		
		public void resetUI()
		{
			// reset component visibility
			setPlaceholderVisibility(false);
			
			pnlWeightSelectionUI.setVisible(true);
			pnlFooterUI.setVisible(true);
			
			lblDocTitleUI.setVisible(true);
			lblDocLevelUI.setVisible(true);
			lblDocNumSentencesUI.setVisible(true);
			lblDocNumWordsUI.setVisible(true);
			icoHelpTextUI.setVisible(true);		
		}
		
		public void reload(boolean fullReload)
		{
			resetUI();
			
			switch (type)
			{
			case RANKABLE:
				{
					if (weightSelection == null)
						weightSelection = new Table(TableType.WEIGHT_SELECTION, null);
					if (constructionDetails == null)
						constructionDetails = new Table(TableType.ALL_CONSTRUCTIONS, null);
					
					// remove current tables from the pane
					pnlWeightSelectionUI.remove(weightSelection.table);
					pnlConstructionDetailsUI.remove(constructionDetails.table);
					
					weightSelection.dataProvider.removeDataDisplay(weightSelection.table);
					constructionDetails.dataProvider.removeDataDisplay(constructionDetails.table);
					
					if (fullReload)
						genTableData(weightSelection.dataProvider.getList(), constructionDetails.dataProvider.getList());
					
					// re-init tables
					weightSelection = new Table(TableType.WEIGHT_SELECTION, weightSelection.dataProvider);
					constructionDetails = new Table(TableType.ALL_CONSTRUCTIONS, constructionDetails.dataProvider);
					
					LocalizationData ld = getLocalizationData(localeCore.getLanguage());
					
					weightSelection.initColumns(ld.get(DocumentPreviewPaneLocale.DESC_tableColConstruction),
							ld.get(DocumentPreviewPaneLocale.DESC_tableColHits),
							ld.get(DocumentPreviewPaneLocale.DESC_tableColWeight));
					constructionDetails.initColumns(ld.get(DocumentPreviewPaneLocale.DESC_tableColConstruction),
							ld.get(DocumentPreviewPaneLocale.DESC_tableColHits),
							ld.get(DocumentPreviewPaneLocale.DESC_tableColRelFreq));
					
					// add them to the pane
					pnlWeightSelectionUI.add(weightSelection.table);
					pnlConstructionDetailsUI.add(constructionDetails.table);
					
					// set up the remaining fields
					lblDocTitleUI.setText(rankable.getDocument().getTitle());
					lblDocLevelUI.setText(rankable.getDocument().getReadabilityLevel().toString());
					lblDocNumSentencesUI.setText(rankable.getDocument().getNumSentences() + ld.get(DocumentPreviewPaneLocale.DESC_lblDocNumSentences));
					lblDocNumWordsUI.setText(rankable.getDocument().getNumWords() + ld.get(DocumentPreviewPaneLocale.DESC_lblDocNumWords));
					htmlDocTextPreviewUI.setHTML(rankable.getPreviewMarkup());
					
					break;
				}
			case UNRANKABLE:
				{
					// hide unused widgets
					pnlWeightSelectionUI.setVisible(false);
					pnlFooterUI.setVisible(false);
					lblDocLevelUI.setVisible(false);
					lblDocNumSentencesUI.setVisible(false);
					lblDocNumWordsUI.setVisible(false);
					icoHelpTextUI.setVisible(false);
					
					// update the rest
					lblDocTitleUI.setText(unrankable.getTitle());
					htmlDocTextPreviewUI.setHTML(new SafeHtmlBuilder().appendEscapedLines(unrankable.getText()).toSafeHtml());
					
					break;
				}
			}
		
		}
	}
	
	private void setPlaceholderVisibility(boolean visible)
	{
		pnlPlaceholderUI.setVisible(visible);
		pnlPreviewContainerUI.setVisible(visible == false);
	}
	
	private void initLocale()
	{
		lblPlaceholderTextLC = new SimpleLocalizedTextWidget<>(lblPlaceholderTextUI, DocumentPreviewPaneLocale.DESC_lblPlaceholderText);
		icoHelpTextLC = new SimpleLocalizedWidget<>(icoHelpTextUI, DocumentPreviewPaneLocale.DESC_icoHelpText, (w, s) -> {
			w.setTitle(s);
		});
		tglConstructionDetailsLC = new SimpleLocalizedTextWidget<>(tglConstructionDetailsUI, DocumentPreviewPaneLocale.DESC_tglConstructionDetails);
		
		registerLocale(DocumentPreviewPaneLocale.INSTANCE.en);
		registerLocale(DocumentPreviewPaneLocale.INSTANCE.de);
		
		registerLocalizedWidget(lblPlaceholderTextLC);
		registerLocalizedWidget(icoHelpTextLC);
		registerLocalizedWidget(tglConstructionDetailsLC);
		
		refreshLocalization();
	}
	
	private void initUI()
	{
		setPlaceholderVisibility(true);
	}
	
	public DocumentPreviewPane()
	{
		super(ClientEndPoint.get().getLocalization());
		state = new State();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		initLocale();
		initUI();
	}
	
	
	@Override
	public void setLocalization(Language lang)
	{
		super.setLocalization(lang);
		state.reload(false);
	}

	@Override
	public void show(Rankable input) {
		state.init(input);
	}

	@Override
	public void show(UnRankable input) {
		state.init(input);
	}
	
	@Override
	public void hide()
	{
		// just hide the main panel
		setPlaceholderVisibility(true);
	}
}
