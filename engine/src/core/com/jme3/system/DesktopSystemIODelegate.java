package com.jme3.system;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.jme3.texture.Image;
import com.jme3.texture.image.DefaultImageRaster;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.Screenshots;

public class DesktopSystemIODelegate extends AbstractSystemIODelegate {
    protected final Logger logger = Logger.getLogger(DesktopSystemIODelegate.class.getName());
    protected boolean initialized = false;
    protected boolean isLowPermissions = false;
    protected File storageFolder = null;

	@Override
	public ImageRaster createImageRaster(Image image, int slice) {
        assert image.getEfficentData() == null;
        return new DefaultImageRaster(image, slice);
	}

	@Override
	public void setLowPermissions(boolean isLowPermissions) {
		this.isLowPermissions = isLowPermissions;
	}

	@Override
	public boolean isLowPermissions() {
		return this.isLowPermissions;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
        return this.getClass().getResourceAsStream(name);
	}

	@Override
	public URL getResource(String name) {
        return this.getClass().getResource(name);
	}

	@Override
	public synchronized File getStorageFolder() {
        if (isLowPermissions) {
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
	public void writeImageFile(	OutputStream outStream, 
								String format,
								ByteBuffer imageData, 
								int width, 
								int height) throws IOException {
        BufferedImage awtImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Screenshots.convertScreenShot(imageData, awtImage);
        ImageIO.write(awtImage, format, outStream);
	}

	@Override
    public void initialize(AppSettings settings) {
        if (initialized) {
            return;
        }

        initialized = true;
        try {
            if (!isLowPermissions) {
                // can only modify logging settings
                // if permissions are available
//                JmeFormatter formatter = new JmeFormatter();
//                Handler fileHandler = new FileHandler("jme.log");
//                fileHandler.setFormatter(formatter);
//                Logger.getLogger("").addHandler(fileHandler);
//                Handler consoleHandler = new ConsoleHandler();
//                consoleHandler.setFormatter(formatter);
//                Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
//                Logger.getLogger("").addHandler(consoleHandler);
            }
//        } catch (IOException ex){
//            logger.log(Level.SEVERE, "I/O Error while creating log file", ex);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "Security error in creating log file", ex);
        }
        logger.log(Level.INFO, "Running on {0}", JmeSystem.getFullName());

        if (!isLowPermissions) {
            try {
                Natives.extractNativeLibs(JmeSystem.getPlatform(), settings);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error while copying native libraries", ex);
            }
        }
    }
}
