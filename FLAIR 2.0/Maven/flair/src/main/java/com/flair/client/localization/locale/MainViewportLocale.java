package com.flair.client.localization.locale;

import com.flair.client.localization.SimpleLocale;

public final class MainViewportLocale extends SimpleLocale
{
	public static final String		DESC_btnWebSearchUI = "btnWebSearchUI";
	public static final String		DESC_btnUploadUI = "btnUploadUI";
	public static final String		DESC_btnAboutUI = "btnAboutUI";
	public static final String		DESC_btnHistoryUI = "btnHistoryUI";
	public static final String		DESC_btnSwitchLangUI = "btnSwitchLangUI";
	public static final String		DESC_btnCloseWebSearchUI = "btnCloseWebSearchUI";
	public static final String		DESC_icoSettingsMorphUI = "icoSettingsMorphUI";
	
	public static final String		DESC_defSearchTitle = "defSearchTitle";
	public static final String		DESC_defSearchCaption = "defSearchCaption";
	public static final String		DESC_defConfigTitle = "defConfigTitle";
	public static final String		DESC_defConfigCaption = "defConfigCaption";
	public static final String		DESC_defUploadTitle = "defUploadTitle";
	public static final String		DESC_defUploadCaption = "defUploadCaption";
	
	public static final String		DESC_OpInProgessTitle = "OpInProgessTitle";
	public static final String		DESC_OpInProgessCaption = "OpInProgessCaption";

	@Override
	public void init()
	{
		// EN
		en.put(DESC_btnWebSearchUI, "Web Search");
		en.put(DESC_btnUploadUI, "Upload Corpus");
		en.put(DESC_btnAboutUI, "About FLAIR");
		en.put(DESC_btnHistoryUI, "Recent Analyses");
		en.put(DESC_btnSwitchLangUI, "Switch Interface Language");
		en.put(DESC_btnCloseWebSearchUI, "Cancel");
		en.put(DESC_icoSettingsMorphUI, "Settings");
		
		en.put(DESC_defSearchTitle, "Search");
		en.put(DESC_defSearchCaption, "Click on the Search icon below and type in a query. FLAIR will fetch the top results from the Bing Search Engine.");
		en.put(DESC_defConfigTitle, "Configure");
		en.put(DESC_defConfigCaption, "Configure the settings: text (complexity, length) and language (the passive, wh- questions, academic vocabulary, ...). You can export the settings to apply them to all further searches. FLAIR will re-rank the documents according to the configured settings. Click on the link to open the page in a new tab or read the enhanced text in the right-side panel.");
		en.put(DESC_defUploadTitle, "Upload");
		en.put(DESC_defUploadCaption, "Upload custom documents and corpora. FLAIR will analyse and rank their content according to your settings.");
		en.put(DESC_OpInProgessTitle, "Confirmation");
		en.put(DESC_OpInProgessCaption, "A previous operation is in progress. Are you sure you want to continue? This will cancel the currently executing operation.");
		
		
		// DE
		de.put(DESC_btnWebSearchUI, "Internetsuche");
		de.put(DESC_btnUploadUI, "Text hochladen");
		de.put(DESC_btnAboutUI, "Über FLAIR");
		de.put(DESC_btnHistoryUI, "Neuanalysen");
		de.put(DESC_btnSwitchLangUI, "Anzeigesprache wechseln");
		de.put(DESC_btnCloseWebSearchUI, "Abbrechen");
		de.put(DESC_icoSettingsMorphUI, "Einstellungen");
		
		de.put(DESC_defSearchTitle, "Suchen");
		de.put(DESC_defSearchCaption, "Klicken Sie auf das Suchsymbol unten und geben Sie eine Suchanfrage ein. FLAIR wird die obersten Ergebnisse von der Bing Suchmaschine abrufen.");
		de.put(DESC_defConfigTitle, "Konfigurieren");
		de.put(DESC_defConfigCaption, "Konfgurieren Sie die Einstellungen: Text (Länge, Schwierigkeitsgrad) und Grammatik (Passiv, W-Fragen, akademisches Vokabular, ...). Sie können die Einstellungen auch exportieren, um sie auf spätere Suchen anzuwenden. FLAIR bewertet die Suchergebnisse entsprechend Ihrer Einstellungen neu. Klicken Sie auf den Link, um die Seite in einem neuen Tab zu öffnen, oder lesen Sie den extrahierten, erweiterten Text im Textfeld auf der rechten Seite.");
		de.put(DESC_defUploadTitle, "Hochladen");
		de.put(DESC_defUploadCaption, "Laden Sie Ihre Dateien hoch. FLAIR wird sie analysieren und Ihren Einstellungen entsprechend bewerten.");
		de.put(DESC_OpInProgessTitle, "Bestätigung");
		de.put(DESC_OpInProgessCaption, "Ein vorheriger Vorgang läuft noch. Möchten Sie wirklich fortsetzen? Der andere Vorgang wird storniert.");
	}
	
	public static final MainViewportLocale		INSTANCE = new MainViewportLocale();
}