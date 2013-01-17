package com.jme3.system.android;

import android.app.Activity;
import android.app.AlertDialog;

import com.jme3.system.AbstractSystemDialogDelegate;
import com.jme3.system.AppSettings;

public class AndroidSystemDialogDelegate extends AbstractSystemDialogDelegate {

	@Override
	public boolean showSettingsDialog(AppSettings settings, boolean loadDefaults) {
		return true;
	}

	@Override
	public void showErrorDialog(String message) {
        final String finalMsg = message;
        final String finalTitle = "Error in application";
        final Activity context = JmeAndroidSystem.getActivity();

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(finalTitle).setMessage(finalMsg).create();
                dialog.show();
            }
        });
	}

}
