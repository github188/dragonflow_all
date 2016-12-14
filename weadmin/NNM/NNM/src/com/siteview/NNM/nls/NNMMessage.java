package com.siteview.NNM.nls;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.rap.rwt.RWT;

public class NNMMessage {
	private static final String MESSAGE_BUNDLE = "OSGI-INF/I18N/NNMMessage";

	public static String getvalue(String key) {
		return getvalue(key, RWT.getLocale());
	}

	public static String getvalue(String key, Locale local) {
		return ResourceBundle.getBundle(MESSAGE_BUNDLE, local).containsKey(key) ? ResourceBundle.getBundle(MESSAGE_BUNDLE, local).getString(key) : key;
	}

	private NNMMessage() {

	}

	public static NNMMessage get() {
		return (NNMMessage) RWT.NLS.getISO8859_1Encoded(MESSAGE_BUNDLE, NNMMessage.class);
	}
}
