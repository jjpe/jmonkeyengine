package com.jme3.system;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import mockit.Expectations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jme3.app.SettingsDialog;
import com.jme3.asset.AssetNotFoundException;

public class DesktopSystemDialogDelegateTest {
	DesktopSystemDialogDelegate delegate;
	
	@Before
	public void setup() {
		delegate = new DesktopSystemDialogDelegate();
	}
	/**
	 * Tests if showErrorMessageDialog shows a {@link JOptionPane#ERROR_MESSAGE}
	 * with the proper message.
	 */
	@Test
	public void testShowErrorMessageDialog() {
		final String msg = "Hello, I'm testing errors!";
		// make the error message 'appear' immediately, or the test might end before it does.
		new MakeInvokeLaterImmediate();
		new Expectations() {
			JOptionPane mock = null;
			{
				JOptionPane.showMessageDialog(null, msg, (String) any, JOptionPane.ERROR_MESSAGE);
				times=1;
			}
		};
		
		delegate.showErrorDialog(msg);
	}
	
	/**
	 * Tests showSettingsDialog when the system is on an Event Dispatch Thread.
	 */
	@Test(expected=IllegalStateException.class)
	public void testShowSettingsDialogOnEDT() {
		new Expectations() {
			@SuppressWarnings("unused")
			SwingUtilities util;
			{
				SwingUtilities.isEventDispatchThread(); result=true;
			}
		};
		delegate.showSettingsDialog(new AppSettings(false), false);
	}
	
	/**
	 * Tests showSettingsDialog with a nonexistent icon.
	 */
	@Test(expected=AssetNotFoundException.class)
	public void testShowSettingsDialogWithWrongIcon() {
		AppSettings set = new AppSettings(false);
		set.setSettingsDialogImage("com/jme3/system/derpy/shouldnt/be/there.pngxrs");
		delegate.showSettingsDialog(set, false);
	}
	
	/**
	 * Tests showSettingsDialog with an absolute path to an icon.
	 */
	@Test
	public void testShowSettingsDialogWithPrefixedIcon() {
		AppSettings set = new AppSettings(false);
		set.setSettingsDialogImage("com/jme3/system/Monkey.png");
		new PreventSettingsDialog(SettingsDialog.APPROVE_SELECTION);
		boolean accepted = delegate.showSettingsDialog(set, false);
		Assert.assertTrue("The selection was not approved", accepted);
	}
	
	/**
	 * Tests showSettingsDialog with a relative path to an icon.
	 */
	@Test
	public void testShowSettingsDialogWithoutPrefixedIcon() {
		AppSettings set = new AppSettings(false);
		set.setSettingsDialogImage("com/jme3/system/Monkey.png");
		new PreventSettingsDialog(SettingsDialog.CANCEL_SELECTION);
		boolean accepted = delegate.showSettingsDialog(set, false);
		Assert.assertFalse("The selection was not canceled", accepted);
	}
	
}
