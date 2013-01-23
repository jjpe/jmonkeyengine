package com.jme3.system;

public abstract class AbstractSystemStateDelegate implements ISystemStateDelegate {

	@Override
	public boolean trackDirectMemory() {
		return false;
	}

	@Override
	public String getFullName() {
		return JmeVersion.FULL_NAME;
	}

	@Override
	abstract public Platform getPlatform();
	
	@Override
	public void initialize(AppSettings settings) {
	}
}
