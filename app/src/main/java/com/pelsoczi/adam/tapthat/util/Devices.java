package com.pelsoczi.adam.tapthat.util;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

/**
 * Any helper method for device itself (screen, settings, etc.)
 */
public final class Devices {
	/**
	 * @param context
	 * @param windowToken Use {@link android.view.View#getWindowToken()} or {@code Activity.getCurrentFocus().getWindowToken()}
	 */
	public static void removeKeyboard(final Context context, final IBinder windowToken) {
		if (context == null || windowToken == null) {
			return;
		}
		final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
	}
}
