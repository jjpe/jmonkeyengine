package com.jme3.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.Screenshots;

/**
 * The {@link ISystemIODelegate} is an interface to the main IO
 * features of the system.
 * It also contains the permission state of the system,
 * as it is mainly used to check if IO is allowed.
 * 
 * @author Volker Lanting
 *
 */
public interface ISystemIODelegate {

	/**
	 * Creates an {@link ImageRaster} from an image and a slice.
	 * @param img the {@link Image}.
	 * @param slice the part of the image to use.
	 * @return an ImageRaster which can be used to conveniently retrieve pixels.
	 */
	ImageRaster createImageRaster(Image image, int slice);
	/**
	 * Set whether the system has low permissions.
	 * @param newVal true if the system has low permissions.
	 */
	void setLowPermissions(boolean newVal);
	/**
	 * Checks if the system has low permissions,
	 * mainly meaning that Natives won't be extracted and 
	 * the storageFolder can not be obtained.
	 * @return true iff the system has low permissions.
	 */
	boolean isLowPermissions();
	/**
	 * Returns an InputStream to read the resource,
	 * or null if there was no such resource.
	 * @param name the name of the resource.
	 * @return an InputStream or null.
	 */
	InputStream getResourceAsStream(String name);
	/**
	 * Returns the URL of the given resource,
	 * or null if there was no such resource.
	 * @param name the name of the resource (e.g. file path).
	 * @return the URL of the resource or null.
	 */
	URL getResource(String name);
	/**
	 * Retrieves the storage folder used by jme
	 * if the system has high enough permissions.
	 * @return the storage folder.
	 */
	File getStorageFolder();
	/**
	 * Converts and writes the image to the given OutputStream.
	 * See {@link Screenshots#convertScreenShot(ByteBuffer, java.awt.image.BufferedImage)}
	 * for conversion details. 
	 * @param out the {@link OutputStream} to write the image to.
	 * @param format the informal format of the image. 
	 * @param imageData the actual data buffer containing the pixel data.
	 * @param width the width of the image.
	 * @param height the height of the image.
	 * 
	 * @throws IOException
	 */
	void writeImageFile(	OutputStream out,
							String format,
							ByteBuffer imageData,
							int width,
							int height) throws IOException;
	
	/**
	 * Initializes the delegate.
	 * @param settings the settings required for initialization.
	 */
	void initialize(AppSettings settings);
}
