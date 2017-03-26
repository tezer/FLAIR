package com.flair.client.localization.locale;

import com.flair.client.localization.SimpleLocale;

public final class MainViewportLocale extends SimpleLocale
{
	public static final String		DESC_btnWebSearchUI = "btnWebSearchUI";
	public static final String		DESC_btnUploadUI = "btnUploadUI";
	public static final String		DESC_btnAboutUI = "btnAboutUI";
	public static final String		DESC_btnSwitchLangUI = "btnSwitchLangUI";
	public static final String		DESC_btnLangEnUI = "btnLangEnUI";
	public static final String		DESC_btnLangDeUI = "btnLangDeUI";
	
	public static final String		DESC_txtSearchBoxUI = "txtSearchBoxUI";
	public static final String		DESC_selResultCountItm10UI = "selResultCountItm10UI";
	public static final String		DESC_selResultCountItm20UI = "selResultCountItm20UI";
	public static final String		DESC_selResultCountItm30UI = "selResultCountItm30UI";
	public static final String		DESC_selResultCountItm40UI = "selResultCountItm40UI";
	public static final String		DESC_selResultCountItm50UI = "selResultCountItm50UI";
	public static final String		DESC_selResultLangItmEnUI = "selResultLangItmEnUI";
	public static final String		DESC_selResultLangItmDeUI = "selResultLangItmDeUI";
	
	public static final String		DESC_defSearchTitle = "defSearchTitle";
	public static final String		DESC_defSearchCaption = "defSearchCaption";
	public static final String		DESC_defConfigTitle = "defConfigTitle";
	public static final String		DESC_defConfigCaption = "defConfigCaption";
	public static final String		DESC_defReadTitle = "defReadTitle";
	public static final String		DESC_defReadCaption = "defReadCaption";

	@Override
	public void init()
	{
		// EN
		en.put(DESC_btnWebSearchUI, "Web Search");
		en.put(DESC_btnUploadUI, "Upload Corpus");
		en.put(DESC_btnAboutUI, "About FLAIR");
		en.put(DESC_btnSwitchLangUI, "Switch Interface Language");
		en.put(DESC_btnLangEnUI, "English");
		en.put(DESC_btnLangDeUI, "German");
		en.put(DESC_txtSearchBoxUI, "Enter a query");
		en.put(DESC_selResultCountItm10UI, "10 Results");
		en.put(DESC_selResultCountItm20UI, "20 Results");
		en.put(DESC_selResultCountItm30UI, "30 Results");
		en.put(DESC_selResultCountItm40UI, "40 Results");
		en.put(DESC_selResultCountItm50UI, "50 Results");
		en.put(DESC_selResultLangItmEnUI, "English");
		en.put(DESC_selResultLangItmDeUI, "German");
		en.put(DESC_defSearchTitle, "Search");
		en.put(DESC_defSearchCaption, "Click on the Search icon on top and type in a query. FLAIR will fetch the top results from the Bing Search Engine.");
		en.put(DESC_defConfigTitle, "Configure");
		en.put(DESC_defConfigCaption, "Configure the settings: text (complexity, length) and language (the passive, wh- questions, academic vocabulary, ...). You can export the settings to apply them to all further searches.");
		en.put(DESC_defReadTitle, "Read");
		en.put(DESC_defReadCaption, "FLAIR will re-rank the documents according to the configured settings. Click on the link to open the page in a new tab or read the enhanced text in the right-side panel.");
		
		
		// DE
		de.put(DESC_btnWebSearchUI, "Internet Suche");
		de.put(DESC_btnUploadUI, "Text Hochladen");
		de.put(DESC_btnAboutUI, "Über FLAIR");
		de.put(DESC_btnSwitchLangUI, "Anzeigesprache wechseln");
		de.put(DESC_btnLangEnUI, "Englisch");
		de.put(DESC_btnLangDeUI, "Deutsch");
		de.put(DESC_txtSearchBoxUI, "Suchbegriff eingeben");
		de.put(DESC_selResultCountItm10UI, "10 Seiten");
		de.put(DESC_selResultCountItm20UI, "20 Seiten");
		de.put(DESC_selResultCountItm30UI, "30 Seiten");
		de.put(DESC_selResultCountItm40UI, "40 Seiten");
		de.put(DESC_selResultCountItm50UI, "50 Seiten");
		de.put(DESC_selResultLangItmEnUI, "Englisch");
		de.put(DESC_selResultLangItmDeUI, "Deutsch");
		de.put(DESC_defSearchTitle, "Suchen");
		de.put(DESC_defSearchCaption, "Klicken Sie auf das Suchsymbol oben und geben Sie eine Suchanfrage ein. FLAIR wird die obersten Ergebnisse von der Suchmaschine Bing abrufen.");
		de.put(DESC_defConfigTitle, "Konfigurieren");
		de.put(DESC_defConfigCaption, "Konfgurieren Sie die Einstellungen: Text (Länge, Schwierigkeitsgrad) und Grammatik (Passiv, Wh-Fragen, akademisches Vokabular, ...). Sie können die Einstellungen auch exportieren, um sie auf spätere Suchen anzuwenden.");
		de.put(DESC_defReadTitle, "Lesen");
		de.put(DESC_defReadCaption, "FLAIR bewertet die Suchergebnisse neu entsprechend Ihren Einstellungen. Klicken Sie auf den Link, um die Seite in einem neuen Tab zu öffnen, oder lesen Sie den extrahierten erweiterten Text im Textfeld auf der rechten Seite.");
		
	}
	
	public static final MainViewportLocale		INSTANCE = new MainViewportLocale();
}