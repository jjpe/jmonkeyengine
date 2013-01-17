package com.jme3.system;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.DefaultImageRaster;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;
import com.jme3.util.Screenshots;

public class DesktopSystemIODelegateTest {
	DesktopSystemIODelegate delegate;
	
	@Before
	public void setup() {
		delegate = new DesktopSystemIODelegate();
	}
	
	/**
	 * Checks if createImageRaster creates a DefaultImageRaster with
	 * correct pixels. 
	 */
	@Test
	public void testCreateImageRaster() {
		Image img = new Image();
		int w = 5;
		int h = 3;
		img.setHeight(h);
		img.setWidth(w);
		img.setFormat(Format.RGBA8);
		final ByteBuffer buff = BufferUtils.createByteBuffer(h*w*4);
		int pixel00 = 0xFFF00F00;
		int pixel21 = 0xAA996655;
		// apparently putting the int as bytes reverses the byte order
		buff.putInt(0, Integer.reverseBytes(pixel00));
		buff.putInt(1*w*4+2*4, Integer.reverseBytes(pixel21));
		img.setData(buff);
		// select the buffer we gave
		int slice = 0;
		ImageRaster raster = delegate.createImageRaster(img, slice);
		Assert.assertEquals("did not create a DefaultImageRaster",
				DefaultImageRaster.class, raster.getClass());
		Assert.assertEquals("pixel (0,0) wasnt represented properly in this raster",
				pixel00, raster.getPixel(0, 0).asIntRGBA());
		Assert.assertEquals("pixel (2,1) wasnt represented properly in this raster",
				pixel21, raster.getPixel(2, 1).asIntRGBA());
	}
	
	/**
	 * Tests if writeImageFile calls ImageIO.write(RenderedImage,String,OutputStream)
	 * to write the image.
	 * It also checks if the image got converted properly.
	 * @param os should be mocked by JMockit
	 */
	@Test
	public void testWriteImageFile(final OutputStream os) throws IOException {
		final String format = "jpg";
		final int width = 2;
		final int height = 2;
		final ByteBuffer buff = BufferUtils.createByteBuffer(width*height*4);
		buff.putInt(0, 0xFFF00F00);
		buff.putInt(3, 0xAA996655);
		new MockUp<ImageIO>() {
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			boolean write(RenderedImage img, String f, OutputStream out) {
				Assert.assertEquals("The image format changed",format, f);
				Assert.assertEquals("The output stream changed",os, out);
				Assert.assertEquals("The height changed", height, img.getHeight());
				Assert.assertEquals("The width changed", width, img.getWidth());
				BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
				Screenshots.convertScreenShot(buff, img2);
				byte[] buffExpected = ((DataBufferByte)img2.getData().getDataBuffer()).getData();
				byte[] buffActual = ((DataBufferByte)img.getData().getDataBuffer()).getData();
				Assert.assertEquals("buffers have unequal length",buffExpected.length, buffActual.length);
				for (int i = 0; i < buffExpected.length; i++) {
					Assert.assertEquals("byte number "+i+" did not get properly convered",
							buffExpected[i], buffActual[i]);
				}
				return false;
			}
		};
		delegate.writeImageFile(os, format, buff, width, height);
	}
}
