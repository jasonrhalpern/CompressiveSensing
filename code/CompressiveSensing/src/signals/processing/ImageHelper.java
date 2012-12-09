/** \file
*/
package signals.processing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageHelper {

	public static void printPixelARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
	}

	public static void printImage(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		System.out.println("width, height: " + w + ", " + h);

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				System.out.println("x,y: " + j + ", " + i);
				int pixel = image.getRGB(j, i);
				printPixelARGB(pixel);
				System.out.println("");
			}
		}
	}

	public static BufferedImage createResizedCopy(Image originalImage, 
    		int scaledWidth, int scaledHeight, 
    		boolean preserveAlpha)
	{
    		System.out.println("resizing...");
	    	int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
	    	Graphics2D g = scaledBI.createGraphics();
    		if (preserveAlpha) {
	    		g.setComposite(AlphaComposite.Src);
    		}
	    	g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
    		g.dispose();
	    	return scaledBI;
	}

	public static BufferedImage convertToGray(BufferedImage bi){
		int heightLimit = bi.getHeight();
		int widthLimit = bi.getWidth();
		BufferedImage converted = new BufferedImage(widthLimit, heightLimit,
	        			BufferedImage.TYPE_BYTE_GRAY);

		for(int height = 0; height < heightLimit; height++){
	        	for(int width = 0; width < widthLimit; width++){
	        	    	/**
				 Remove the alpha component
				*/
		            	Color c = new Color(bi.getRGB(width, height) & 0x00ffffff);
	        	    	/**
				Normalize
				*/
		            	int newRed = (int) (0.2989f * c.getRed());
	        	   	int newGreen = (int) (0.5870f * c.getGreen());
	            		int newBlue = (int) (0.1140f * c.getBlue());
		            	int roOffset = newRed + newGreen + newBlue;
		            	converted.setRGB(width, height, roOffset);
	        	}
	    	}
	    	return converted;
	}
}
