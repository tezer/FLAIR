package com.flair.client.localization.interfaces;

import com.flair.client.localization.LocalizationData;
import com.flair.shared.grammar.Language;

/*
 * Interface implemented by all localized views
 */
public interface LocalizedUI
{
	public LocalizationData			getLocalizationData(Language lang);		// gets the locale data for the lang
	public void						setLocalization(Language lang);			// updates the view's locale
}