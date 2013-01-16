package com.jme3.system;

import com.jme3.input.SoftTextDialogInput;

/**
 * Abstract parent for interacting with Dialogs.
 * 
 * @author Volker Lanting
 * @author Joey Ezechiels
 */
public abstract class AbstractSystemDialogDelegate implements ISystemDialogDelegate {
	
	protected SoftTextDialogInput softTextDialogInput = null;
	
	@Override
	public SoftTextDialogInput getSoftTextDialogInput() {
		return softTextDialogInput;
	}
	
	@Override
	public void setSoftTextDialogInput(SoftTextDialogInput input) {
		this.softTextDialogInput = input;
	}
	
	@Override
	public void initialize(AppSettings settings) {}
	
}
