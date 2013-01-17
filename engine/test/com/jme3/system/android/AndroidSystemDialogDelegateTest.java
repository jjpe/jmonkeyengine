package com.jme3.system.android;

import mockit.NonStrictExpectations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.app.AlertDialog;

import com.jme3.system.AppSettings;

public class AndroidSystemDialogDelegateTest {
	private AndroidSystemDialogDelegate delegate;
	
	@Before
	public void setup() {
		delegate = new AndroidSystemDialogDelegate();
	}
	
	@Test
	public void showErrorDialogTest(Activity a) {
		final String msg = "Hello, I'm testing errors!";
		// make the error message 'appear' immediately, or the test might end before it does.
		new ForceInvoke();
		new NonStrictExpectations() {
			AlertDialog mock;
			AlertDialog.Builder b;
			{
				b.setTitle(anyString);result=b;
				b.setMessage(msg);result=b;times=1;
				b.create();result=mock;
				mock.show(); times=1;
			}
		};
		JmeAndroidSystem.setActivity(a);
		delegate.showErrorDialog(msg);
	}
	
	
	/**
	 * Tests showSettingsDialog, which returns a flag.
	 */
	@Test
	public void showSettingsDialogTest() {
		boolean b = delegate.showSettingsDialog(new AppSettings(false), false);
		Assert.assertTrue("showSettings should be a stub returning true on AndroidSytem", b);
	}
}
