package com.jme3.system;

import com.jme3.input.SoftTextDialogInput;

/**
 * The {@link ISystemDialogDelegate} is an interface to the main UI
 * features of the system. It deals with showing dialogs and contains
 * a SoftTextDialogInput state.
 * @author Volker Lanting
 *
 */
public interface ISystemDialogDelegate {
	/**
	 * Displays a window to select AppSettings.
	 * @param settings the settings. They will be updated with the settings the user chose.
	 * @param loadDefaults whether the default settings should be loaded.
	 * @return true iff the user accepted the settings.
	 */
	boolean showSettingsDialog(AppSettings settings, boolean loadDefaults);
	/**
	 * Displays an error dialog with the given error message.
	 * @param message the message to display.
	 */
	void showErrorDialog(String message);
	/**
	 * Sets the {@link SoftTextDialogInput} that is used by this system (if any).
	 * @param input the SoftTextDialogInput to use for this system. 
	 */
	void setSoftTextDialogInput(SoftTextDialogInput input);
	/**
	 * Retrieves the {@link SoftTextDialogInput} for this system.
	 * @return the SoftTextDialog input that was set for this system.
	 * May be null.
	 */
	SoftTextDialogInput getSoftTextDialogInput();
}
