package cs211;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageProcessing extends PApplet {
	PImage img;
	int lowThreshold;
	int highThreshold;
	HScrollbar lowThresholdBar;
	HScrollbar highThresholdBar;

	public void setup() {
		size(800, 600);
		img = loadImage("board1.jpg");

		img = convolute(img);

		lowThresholdBar = new HScrollbar(this, 0, 580, 800, 20);
		highThresholdBar = new HScrollbar(this, 0, 540, 800, 20);
		// noLoop(); // no interactive behaviour: draw() will be called only
		// once.
	}

	public void draw() {
		background(color(0, 0, 0));

		image(colourThr(img), 0, 0);

		lowThresholdBar.display();
		lowThresholdBar.update();

		highThresholdBar.display();
		highThresholdBar.update();

		lowThreshold = (int) (255 * lowThresholdBar.getPos());
		highThreshold = (int) (255 * highThresholdBar.getPos());

		if (lowThresholdBar.getPos() > highThresholdBar.getPos()) {
			int thresholdy = highThreshold;
			highThreshold = lowThreshold;
			lowThreshold = thresholdy;
		}
	}

	public PImage binaryFilter(PImage img) {
		PImage result = createImage(width, height, RGB); // create a new,
															// initially
															// transparent,
															// 'result' image
		for (int i = 0; i < img.width * img.height; i++) {
			// do something with the pixel img.pixels[i]
			float currentBrightness = brightness(img.pixels[i]);
			if (currentBrightness > lowThreshold) {
				result.pixels[i] = color(255);
			} else {
				result.pixels[i] = color(0);
			}
		}
		return result;
	}

	public PImage InvertedBinaryFilter(PImage img) {
		PImage result = createImage(width, height, RGB); // create a new,
															// initially
															// transparent,
															// 'result' image
		for (int i = 0; i < img.width * img.height; i++) {
			// do something with the pixel img.pixels[i]
			float currentBrightness = brightness(img.pixels[i]);
			if (currentBrightness <= lowThreshold) {
				result.pixels[i] = color(255, 120, 50);
			} else {
				result.pixels[i] = color(0);
			}
		}
		return result;
	}

	public PImage colourThr(PImage img) {
		PImage hueMap = createImage(width, height, HSB); // create a new,
															// initially
															// transparent,
															// 'result' image
		for (int i = 0; i < img.width * img.height; i++) {
			float currentHue = hue(img.pixels[i]);
			if (currentHue >= lowThreshold && currentHue <= highThreshold) {
				hueMap.pixels[i] = color(img.pixels[i]);
			} else {
				hueMap.pixels[i] = color(currentHue);
			}
		}
		return hueMap;
	}

	public PImage convolute(PImage img) {
		
		float[][] kernel = { { 0, 0, 0 }, 
							 { 0, 2, 0 }, 
							 { 0, 0, 0 } };
		
		float weight = 1.f;
		int N = kernel.length;
		
		/* create a greyscale image (type: ALPHA) for output */
		PImage result = createImage(img.width + N/2, img.height + N/2, ALPHA);
		
		for(int x = N/2; x < img.width - N/2; x++) {
			for(int y = N/2; y < img.height - N/2; y++) {
				for(int i = x - (N/2); i < x + (N/2) + 1; i++) {
					for(int j = y - (N/2); j < y + (N/2); j++) {
						for(int k = 0; k < N; k++) {
							
						//TODO
							result.pixels[x + y*img.width] = kernel[i][k] * img.pixels[];
					}
				}
			}
			 
			
		}
		return result;
	}
}