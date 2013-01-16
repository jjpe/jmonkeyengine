package com.jme3.system;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.system.JmeContext.Type;

public class DesktopFactoryDelegate implements ISystemFactoryDelegate {

	protected boolean initialized;
	private Logger logger = Logger.getLogger(DesktopFactoryDelegate.class.getName());
	
	@Override
	public JmeContext newContext(AppSettings settings, Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssetManager newAssetManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssetManager newAssetManager(URL configFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AudioRenderer newAudioRenderer(AppSettings setttings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(AppSettings settings) {

	}
}
