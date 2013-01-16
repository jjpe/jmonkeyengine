package com.jme3.system;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.system.JmeContext.Type;

/**
 * A {@link ISystemFactoryDelegate} implementation used for regular desktop-based software.
 *  
 * @author Volker Lanting
 * @author Joey Ezechiels
 */
public class DesktopFactoryDelegate implements ISystemFactoryDelegate {

	protected boolean initialized;
	private Logger logger = Logger.getLogger(DesktopFactoryDelegate.class.getName());
	
	@Override
	public JmeContext newContext(AppSettings settings, Type contextType) {
		initialize(settings);
		JmeContext ctx;
		if (settings.getRenderer() == null
				|| settings.getRenderer().equals("NULL")
				|| contextType == JmeContext.Type.Headless) {
			ctx = new NullContext();
			ctx.setSettings(settings);
		} else if (settings.getRenderer().startsWith("LWJGL")) {
			ctx = newContextLwjgl(settings, contextType);
			ctx.setSettings(settings);
		} else if (settings.getRenderer().startsWith("JOGL")) {
			ctx = newContextJogl(settings, contextType);
			ctx.setSettings(settings);
		} else if (settings.getRenderer().startsWith("CUSTOM")) {
			ctx = newContextCustom(settings, contextType);
			ctx.setSettings(settings);
		} else {
			throw new UnsupportedOperationException(
					"Unrecognizable renderer specified: "
				  + settings.getRenderer());
		}
		return ctx;
	}

    @SuppressWarnings("unchecked")
	private JmeContext newContextLwjgl(AppSettings settings, JmeContext.Type type) {
        try {
            Class<? extends JmeContext> ctxClazz = null;
            switch (type) {
                case Canvas:
                    ctxClazz = (Class<? extends JmeContext>) Class.forName("com.jme3.system.lwjgl.LwjglCanvas");
                    break;
                case Display:
                    ctxClazz = (Class<? extends JmeContext>) Class.forName("com.jme3.system.lwjgl.LwjglDisplay");
                    break;
                case OffscreenSurface:
                    ctxClazz = (Class<? extends JmeContext>) Class.forName("com.jme3.system.lwjgl.LwjglOffscreenBuffer");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported context type " + type);
            }

            return ctxClazz.newInstance();
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Context class is missing!\n"
                    + "Make sure jme3_lwjgl-ogl is on the classpath.", ex);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
	private JmeContext newContextJogl(AppSettings settings, JmeContext.Type type) {
        try {
            Class<? extends JmeContext> ctxClazz = null;
            switch (type) {
                case Display:
                    ctxClazz = (Class<? extends JmeContext>) Class.forName("com.jme3.system.jogl.JoglDisplay");
                    break;
                case Canvas:
                    ctxClazz = (Class<? extends JmeContext>) Class.forName("com.jme3.system.jogl.JoglCanvas");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported context type " + type);
            }

            return ctxClazz.newInstance();
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Context class is missing!\n"
                    + "Make sure jme3_jogl is on the classpath.", ex);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
	private JmeContext newContextCustom(AppSettings settings, JmeContext.Type type) {
        try {
            String className = settings.getRenderer().substring("CUSTOM".length());

            Class<? extends JmeContext> ctxClazz = null;
            ctxClazz = (Class<? extends JmeContext>) Class.forName(className);
            return ctxClazz.newInstance();
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Context class is missing!", ex);
        }

        return null;
    }

	@Override
	public AssetManager newAssetManager() {
        return new DesktopAssetManager(null);
	}

	@Override
	public AssetManager newAssetManager(URL configFile) {
        return new DesktopAssetManager(configFile);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AudioRenderer newAudioRenderer(AppSettings settings) {
        initialize(settings);
        Class<? extends AudioRenderer> clazz = null;
        try {
            if (settings.getAudioRenderer().startsWith("LWJGL")) {
                clazz = (Class<? extends AudioRenderer>) Class.forName("com.jme3.audio.lwjgl.LwjglAudioRenderer");
            } else if (settings.getAudioRenderer().startsWith("JOAL")) {
                clazz = (Class<? extends AudioRenderer>) Class.forName("com.jme3.audio.joal.JoalAudioRenderer");
            } else {
                throw new UnsupportedOperationException(
                        "Unrecognizable audio renderer specified: "
                        + settings.getAudioRenderer());
            }

            AudioRenderer ar = clazz.newInstance();
            return ar;
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Failed to create context", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Audio implementation class is missing!\n"
                    + "Make sure jme3_lwjgl-oal or jm3_joal is on the classpath.", ex);
        }
        return null;
    }

    @Override
    public void initialize(AppSettings settings) {
        if (initialized) return;
        initialized = true;
        logger.log(Level.INFO, "Running on {0}", JmeSystem.getFullName());
        
        if (!JmeSystem.isLowPermissions()) {
            try {
                Natives.extractNativeLibs(JmeSystem.getPlatform(), settings);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error while copying native libraries", ex);
            }
        }
    }
}
