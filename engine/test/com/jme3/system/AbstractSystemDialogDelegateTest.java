package com.jme3.system;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jme3.input.SoftTextDialogInput;

public class AbstractSystemDialogDelegateTest {
	private MyTestSystemDialogDelegate delegate;
	
	@Before
	public void setup() {
		delegate = new MyTestSystemDialogDelegate();
	}
	
	@Test
	public void testSetSoftTextDialogInput(SoftTextDialogInput input) {
		delegate.softTextDialogInput = null;
		delegate.setSoftTextDialogInput(input);
		Assert.assertEquals("the softTextDialogInput was not properly set",
				input, delegate.softTextDialogInput);
	}
	
	/**
	 * Tests if getSoftTextDialogInput retrieves the softDialogInput field.
	 */
	@Test
	public void testGetSoftTextDialogInput(SoftTextDialogInput input) {
		delegate.softTextDialogInput = input;
		Assert.assertEquals("the SoftTextDialogInput was not retrieved properly", 
				input, delegate.getSoftTextDialogInput());
	}
}

class MyTestSystemDialogDelegate extends AbstractSystemDialogDelegate {
	@Override
	public boolean showSettingsDialog(AppSettings settings, boolean loadDefaults) {return false;}
	@Override
	public void showErrorDialog(String message) {}
}