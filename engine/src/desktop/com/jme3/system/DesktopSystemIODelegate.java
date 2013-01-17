package com.jme3.system;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.jme3.texture.Image;
import com.jme3.texture.image.DefaultImageRaster;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.Screenshots;

public class DesktopSystemIODelegate extends AbstractSystemIODelegate {
    protected final Logger logger = Logger.getLogger(DesktopSystemIODelegate.class.getName());

	@Override
	public ImageRaster createImageRaster(Image image, int slice) {
        assert image.getEfficentData() == null;
        return new DefaultImageRaster(image, slice);
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
}
