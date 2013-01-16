package com.jme3.system;

import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;

import com.jme3.asset.AndroidAssetManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.android.AndroidAudioRenderer;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.system.android.OGLESContext;
import com.jme3.util.JmeFormatter;

/**
 * A {@link ISystemFactoryDelegate} used for Android-based software.
 * 
 * @author Joey Ezechiels
 */
public class AndroidFactoryDelegate implements ISystemFactoryDelegate {
    private static Activity activity;
    
    protected final Logger logger = Logger.getLogger(AndroidFactoryDelegate.class.getName());
    protected boolean initialized = false;

	@Override
	public JmeContext newContext(AppSettings settings, Type type) {
        initialize(settings);
        JmeContext ctx = new OGLESContext();
        ctx.setSettings(settings);
        return ctx;
	}

	@Override
	public AssetManager newAssetManager() {
        logger.log(Level.INFO, "Creating asset manager with default config");
        return new AndroidAssetManager(null);
	}

	@Override
	public AssetManager newAssetManager(URL configFile) {
        logger.log(Level.INFO, "Creating asset manager with config {0}", configFile);
        return new AndroidAssetManager(configFile);
	}

	@Override
	public AudioRenderer newAudioRenderer(AppSettings setttings) {
		return new AndroidAudioRenderer(activity);
	}

    public static void setActivity(Activity activity) {
        AndroidFactoryDelegate.activity = activity;
    }

    public static Activity getActivity() {
        return AndroidFactoryDelegate.activity;
    }

	@Override
	public void initialize(AppSettings settings) {
		if (initialized) { return; }
		initialized = true;

		try {
			Logger log = Logger.getLogger(JmeAndroidSystem.class.getName());
			boolean bIsLogFormatSet = false;
			do {
				log.setLevel(Level.ALL);
				if (log.getHandlers().length == 0) {
					log = log.getParent();
					if (log != null) {
						for (Handler h : log.getHandlers()) {
							h.setFormatter(new JmeFormatter());
							h.setLevel(Level.ALL);
							bIsLogFormatSet = true;
						}
					}
				}
			} while (log != null && !bIsLogFormatSet);
		} catch (SecurityException ex) {
			logger.log(Level.SEVERE, "Security error in creating log file", ex);
		}
		logger.log(Level.INFO, "Running on {0}", JmeSystem.getFullName());
	}
}
