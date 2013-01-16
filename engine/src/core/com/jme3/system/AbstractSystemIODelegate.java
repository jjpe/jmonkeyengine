package com.jme3.system;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public abstract class AbstractSystemIODelegate implements ISystemIODelegate {

	protected boolean lowPermissions = false;
	protected File storageFolder;
	
	@Override
	public void setLowPermissions(boolean newVal) {
		lowPermissions = newVal;
	}

	@Override
	public boolean isLowPermissions() {
		return lowPermissions;
	}

	@Override
	public InputStream getResourceAsString(String name) {
		return this.getClass().getResourceAsStream(name);
	}

	@Override
	public URL getResource(String name) {
		return this.getClass().getResource(name);
	}

	@Override
	public File getStorageFolder() {
		if (lowPermissions) {
            throw new UnsupportedOperationException("File system access restricted");
        }
        if (storageFolder == null) {
            // Initialize storage folder
            storageFolder = new File(System.getProperty("user.home"), ".jme3");
            if (!storageFolder.exists()) {
                storageFolder.mkdir();
            }
        }
        return storageFolder;
	}

	@Override
	public void initialize(AppSettings settings) {}
}
