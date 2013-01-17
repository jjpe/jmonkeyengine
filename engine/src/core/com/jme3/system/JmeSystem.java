/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.SoftTextDialogInput;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class JmeSystem {
    private static ISystemDialogDelegate dialogDelegate = null;
    private static ISystemIODelegate ioDelegate = null;
    private static ISystemFactoryDelegate factoryDelegate = null;
    private static ISystemStateDelegate stateDelegate = null;
	
    private static final Map<Class<? extends ISystemDelegate>, List<String>> delegateNameLookupTable =
    		createLookupMap();
	
	private static Map<Class<? extends ISystemDelegate>, List<String>> createLookupMap() {
		Map<Class<? extends ISystemDelegate>, List<String>> map = 
				new HashMap<Class<? extends ISystemDelegate>, List<String>>();
		
		List<String> dAlternatives = new ArrayList<String>();
		List<String> iAlternatives = new ArrayList<String>();
		List<String> fAlternatives = new ArrayList<String>();
		List<String> sAlternatives = new ArrayList<String>();
		
		// First put all the Desktop class names...
		dAlternatives.add("com.jme3.system.DesktopSystemDialogDelegate");
		iAlternatives.add("com.jme3.system.DesktopSystemIODelegate");
		fAlternatives.add("com.jme3.system.DesktopSystemFactoryDelegate");
		
		// ... and only then add the Android class names.
		dAlternatives.add("com.jme3.system.android.AndroidSystemDialogDelegate");
		iAlternatives.add("com.jme3.system.android.AndroidSystemIODelegate");
		fAlternatives.add("com.jme3.system.android.AndroidSystemFactoryDelegate");
		
		// This delegate can be used for both Desktop 
		// and Android, so there's only one entry.
		sAlternatives.add("com.jme3.system.SystemStateDelegate");
		
		map.put(ISystemDialogDelegate.class, dAlternatives);
		map.put(ISystemIODelegate.class, iAlternatives);
		map.put(ISystemFactoryDelegate.class, fAlternatives);
		map.put(ISystemStateDelegate.class, sAlternatives);
		
		return map;
	}
    
    public static void setDelegate(ISystemDialogDelegate dialogDelegate) {
    	JmeSystem.dialogDelegate = dialogDelegate;
    }
    
    public static void setDelegate(ISystemIODelegate ioDelegate) {
    	JmeSystem.ioDelegate = ioDelegate;
    }
    
    public static void setDelegate(ISystemFactoryDelegate factoryDelegate) {
    	JmeSystem.factoryDelegate = factoryDelegate;
    }
    
    public static void setDelegate(ISystemStateDelegate stateDelegate) {
    	JmeSystem.stateDelegate = stateDelegate;
    }
    
    public static synchronized File getStorageFolder() {
        ensureDelegatesAreLoaded();
        return ioDelegate.getStorageFolder();
    }

    public static String getFullName() {
        ensureDelegatesAreLoaded();
        return stateDelegate.getFullName();
    }

    public static InputStream getResourceAsStream(String name) {
        ensureDelegatesAreLoaded();
        return ioDelegate.getResourceAsStream(name);
    }

    public static URL getResource(String name) {
        ensureDelegatesAreLoaded();
        return ioDelegate.getResource(name);
    }

    public static boolean trackDirectMemory() {
        ensureDelegatesAreLoaded();
        return stateDelegate.trackDirectMemory();
    }

    public static void setLowPermissions(boolean lowPerm) {
        ensureDelegatesAreLoaded();
        ioDelegate.setLowPermissions(lowPerm);
    }

    public static boolean isLowPermissions() {
        ensureDelegatesAreLoaded();
        return ioDelegate.isLowPermissions();
    }

    public static void setSoftTextDialogInput(SoftTextDialogInput input) {
        ensureDelegatesAreLoaded();
        dialogDelegate.setSoftTextDialogInput(input);
    }

    public static SoftTextDialogInput getSoftTextDialogInput() {
        ensureDelegatesAreLoaded();
        return dialogDelegate.getSoftTextDialogInput();
    }
    
    public static void writeImageFile(OutputStream outStream, 
    								  String format, 
    								  ByteBuffer imageData, 
    								  int width, 
    								  int height) throws IOException {
        ensureDelegatesAreLoaded();
        ioDelegate.writeImageFile(outStream, format, imageData, width, height);
    }

    public static AssetManager newAssetManager(URL configFile) {
        ensureDelegatesAreLoaded();
        return factoryDelegate.newAssetManager(configFile);
    }

    public static AssetManager newAssetManager() {
        ensureDelegatesAreLoaded();
        return factoryDelegate.newAssetManager();
    }

    public static boolean showSettingsDialog(AppSettings sourceSettings, 
    										 final boolean loadFromRegistry) {
        ensureDelegatesAreLoaded();
        return dialogDelegate.showSettingsDialog(sourceSettings, loadFromRegistry);
    }

    public static Platform getPlatform() {
        ensureDelegatesAreLoaded();
        return stateDelegate.getPlatform();
    }

    public static JmeContext newContext(AppSettings settings, JmeContext.Type contextType) {
        ensureDelegatesAreLoaded();
        return factoryDelegate.newContext(settings, contextType);
    }

    public static AudioRenderer newAudioRenderer(AppSettings settings) {
        ensureDelegatesAreLoaded();
        return factoryDelegate.newAudioRenderer(settings);
    }
    
    public static ImageRaster createImageRaster(Image image, int slice) {
        ensureDelegatesAreLoaded();
        return ioDelegate.createImageRaster(image, slice);
    }

    /**
     * Displays an error message to the user in whichever way the context
     * feels is appropriate. If this is a headless or an offscreen surface
     * context, this method should do nothing. 
     * 
     * @param message The error message to display. May contain new line
     * characters.
     */
    public static void showErrorDialog(String message){
        ensureDelegatesAreLoaded();
        dialogDelegate.showErrorDialog(message);
    }
    
    public static void initialize(AppSettings settings) {
        ensureDelegatesAreLoaded();
        ioDelegate.initialize(settings);
        dialogDelegate.initialize(settings);
        factoryDelegate.initialize(settings);
    }
    
    private static void ensureDelegatesAreLoaded() {
    	JmeSystem.dialogDelegate = 
    			ensureIsLoaded(dialogDelegate, ISystemDialogDelegate.class);
    	JmeSystem.ioDelegate = 
    			ensureIsLoaded(ioDelegate, ISystemIODelegate.class);
    	JmeSystem.factoryDelegate = 
    			ensureIsLoaded(factoryDelegate, ISystemFactoryDelegate.class);
    	JmeSystem.stateDelegate = 
    			ensureIsLoaded(stateDelegate, ISystemStateDelegate.class);
    }
    
	private static <D extends ISystemDelegate> D ensureIsLoaded(final D systemDelegate, Class<D> c) {
		String jmeSystemClassName = JmeSystem.class.getName();
		D returnDelegate = systemDelegate;
		
		if (returnDelegate != null) {
			return returnDelegate;
		}
		
		try {
			final SystemDelegateLoader<D> loader = new SystemDelegateLoader<D>();
			
			for (String delegateName : delegateNameLookupTable.get(c)) {
				returnDelegate = loader.tryToLoad(delegateName);
				if (returnDelegate != null) {
					return returnDelegate;
				}
			}
			
			// None of the system delegates were found ...
			Logger.getLogger(jmeSystemClassName).log(Level.SEVERE,
				  "Failed to find a JmeSystem delegate!\n"
				+ "Ensure either desktop or android jME3 jar is in the classpath.");
		} catch (InstantiationException ex) {
			Logger.getLogger(jmeSystemClassName).log(
					Level.SEVERE, "Failed to create JmeSystem delegate:\n{0}", ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(jmeSystemClassName).log(
					Level.SEVERE, "Failed to create JmeSystem delegate:\n{0}", ex);
		}
		return null; // ... so return null
	}
}

final class SystemDelegateLoader<T extends ISystemDelegate> {
    @SuppressWarnings("unchecked")
	public T tryToLoad(String className) 
			throws InstantiationException, IllegalAccessException {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
