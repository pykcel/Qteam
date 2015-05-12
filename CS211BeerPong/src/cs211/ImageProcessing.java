package cs211;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Math;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class ImageProcessing extends PApplet {
	
	PImage img;
	//HoughTransform h;
	int lowThreshold;
	int highThreshold;
	HScrollbar lowThresholdBar;
	HScrollbar highThresholdBar;

	public void setup() {
		size(800, 600);

		img = loadImage("board1.jpg");

		// lowThresholdBar = new HScrollbar(this, 0, 580, 800, 20);
		// highThresholdBar = new HScrollbar(this, 0, 540, 800, 20);
		
		// noLoop();
	}

	public void draw() {
		background(color(0, 0, 0));

		image(img, 0, 0);
		
		//h.draw();
		
		//image(sobel(convolute(colourThr(img))), 0, 0);
		
		ArrayList<PVector> intersections = getIntersections(hough(sobel(convolute(colourThr(img))), 8));

		// lowThresholdBar.display();
		// lowThresholdBar.update();
		//
		// highThresholdBar.display();
		// highThresholdBar.update();
		//
		// lowThreshold = (int) (255 * lowThresholdBar.getPos());
		// highThreshold = (int) (255 * highThresholdBar.getPos());
		//
		// if (lowThresholdBar.getPos() > highThresholdBar.getPos()) {
		// int thresholdy = highThreshold;
		// highThreshold = lowThreshold;
		// lowThreshold = thresholdy;
		// }
	}

	public PImage binaryFilter(PImage img) {
		PImage result = createImage(width, height, RGB);
		for (int i = 0; i < img.width * img.height; i++) {
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
		PImage result = createImage(width, height, RGB);
		for (int i = 0; i < img.width * img.height; i++) {
			float currentBrightness = brightness(img.pixels[i]);
			if (currentBrightness <= lowThreshold) {
				result.pixels[i] = color(255);
			} else {
				result.pixels[i] = color(0);
			}
		}
		return result;
	}

	public PImage colourThr(PImage img) {

		// Threshold values (low & high) for hue, saturation & brightness.
		float lowSat = 70;
		float highSat = 250;
		float lowHue = 90;
		float highHue = 135;
		float lowBright = 30;
		float highBright = 170;

		PImage hueMap = createImage(width, height, HSB);
		for (int i = 0; i < img.width * img.height; i++) {
			float currentBrightness = brightness(img.pixels[i]);
			float currentSat = saturation(img.pixels[i]);
			float currentHue = hue(img.pixels[i]);
			// Threshold
			if (currentBrightness < lowBright || currentBrightness > highBright
					|| currentSat < lowSat || currentSat > highSat
					|| currentHue < lowHue || currentHue > highHue) {
				hueMap.pixels[i] = color(0);
			} else {
				hueMap.pixels[i] = color(255);
			}
		}

		return hueMap;
	}

	public PImage convolute(PImage img) {

		float[][] kernel = { { 0, 0, 0 }, { 0, 2, 0 }, { 0, 0, 0 } };

		float[][] kernel2 = { { 0, 1, 0 }, { 1, 0, 1 }, { 0, 1, 0 } };

		float[][] kernel3 = { { 9, 12, 9 }, { 12, 15, 12 }, { 9, 12, 9 } };

		float weight = 1.f;

		int N = kernel3.length;

		PImage result = createImage(img.width, img.height, ALPHA);

		for (int x = N / 2; x < img.width - N / 2; x++) {
			for (int y = N / 2; y < img.height - N / 2; y++) {
				for (int i = -N / 2; i <= N / 2; i++) {
					for (int j = -N / 2; j <= N / 2; j++) {
						result.pixels[x + y * img.width] += kernel3[i + (N / 2)][j
								+ (N / 2)]
								* brightness(img.pixels[x + y * img.width + i
										+ j * img.width]);
					}
				}
				result.pixels[x + y * img.width] /= weight;
			}
		}

		return result;
	}

	public PImage sobel(PImage img) {

		float[][] hKernel = { { 0, 1, 0 }, { 0, 0, 0 }, { 0, -1, 0 } };
		float[][] vKernel = { { 0, 0, 0 }, { 1, 0, -1 }, { 0, 0, 0 } };

		PImage result = createImage(img.width, img.height, ALPHA);

		// clear the image
		for (int i = 0; i < img.width * img.height; i++) {
			result.pixels[i] = color(0);
		}

		float max = 0;
		float[] buffer = new float[img.width * img.height];

		// *************************************
		// Implement here the double convolution
		// *************************************
		int N = hKernel.length;

		for (int x = N / 2; x < img.width - N / 2; x++) {
			for (int y = N / 2; y < img.height - N / 2; y++) {
				float sum_h = 0, sum_v = 0;
				for (int i = -N / 2; i <= N / 2; i++) {
					for (int j = -N / 2; j <= N / 2; j++) {
						sum_h += hKernel[i + (N / 2)][j + (N / 2)]
								* brightness(img.pixels[x + y * img.width + i
										+ j * img.width]);
						sum_v += vKernel[i + (N / 2)][j + (N / 2)]
								* brightness(img.pixels[x + y * img.width + i
										+ j * img.width]);
					}
				}
				buffer[x + y * img.width] = sqrt(pow(sum_h, 2) + pow(sum_v, 2));
				if (sqrt(pow(sum_h, 2) + pow(sum_v, 2)) > max) {
					max = sqrt(pow(sum_h, 2) + pow(sum_v, 2));
				}
			}
		}

		for (int y = 2; y < img.height - 2; y++) { // Skip top and bottom edges
			for (int x = 2; x < img.width - 2; x++) { // Skip left and right
				if (buffer[y * img.width + x] > (int) (max * 0.3f)) {
					result.pixels[y * img.width + x] = color(255);
				} else {
					result.pixels[y * img.width + x] = color(0);
				}
			}
		}
		return result;
	}

	public ArrayList<PVector> getIntersections(List<PVector> lines) {
		
		ArrayList<PVector> intersections = new ArrayList<PVector>();
		
		for (int i = 0; i < lines.size() - 1; i++) {
			PVector line1 = lines.get(i);
			for (int j = i + 1; j < lines.size(); j++) {
				PVector line2 = lines.get(j);
				
				// compute the intersection and add it to 'intersections'
				float d = cos(line2.y) * sin(line1.y) - cos(line1.y) * sin(line2.y);
				float x = (line2.x*sin(line1.y)-line1.x*sin(line2.y))/d;
				float y = (-line2.x*cos(line1.y)+line1.x*cos(line2.y))/d;
				
				intersections.add(new PVector(x, y));
				
				// draw the intersection
				fill(255, 128, 0);
				ellipse(x, y, 10, 10);
			}
		}
		return intersections;
	}

	public ArrayList<PVector> hough(PImage edgeImg, int nLines) {

		float discretizationStepsPhi = 0.06f;
		float discretizationStepsR = 2.5f;

		// dimensions of the accumulator
		int phiDim = (int) (Math.PI / discretizationStepsPhi);
		int rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR);
		// our accumulator (with a 1 pix margin around)
		int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];

		// Fill the accumulator: on edge points (ie, white pixels of the edge
		// image), store all possible (r, phi) pairs describing lines going
		// through the point.
		for (int y = 0; y < edgeImg.height; y++) {
			for (int x = 0; x < edgeImg.width; x++) {
				if (brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {
					for (int phi = 0; phi < phiDim; phi++) {
						float realPhi = phi * discretizationStepsPhi;
						float r = (float) ((Math.cos(realPhi) * x) + (Math
								.sin(realPhi) * y));
						int realR =  round(r / discretizationStepsR + (rDim - 1) * 0.5f);
						accumulator[(phi + 1) * (rDim + 2) + realR]++;
					}
				}
			}
		}

		int minVotes = 200;
		HoughComparator comparator = new HoughComparator(accumulator);
		ArrayList<Integer> bestCandidates = new ArrayList<Integer>();

		for (int i = 0; i < accumulator.length; ++i) {
			if (accumulator[i] > minVotes) {
				bestCandidates.add(i);
			}
		}

		// size of the region we search for a local maximum
		int neighbourhood = 20;
		// only search around lines with more that this amount of votes
		// (to be adapted to your image)
		for (int accR = 0; accR < rDim; accR++) {
			for (int accPhi = 0; accPhi < phiDim; accPhi++) {
				// compute current index in the accumulator
				int idx = (accPhi + 1) * (rDim + 2) + accR + 1;
				if (accumulator[idx] > minVotes) {
					boolean bestCandidate = true;
					// iterate over the neighbourhood
					for (int dPhi = -neighbourhood / 2; dPhi < neighbourhood / 2 + 1; dPhi++) {
						// check we are not outside the image
						if (accPhi + dPhi < 0 || accPhi + dPhi >= phiDim)
							continue;
						for (int dR = -neighbourhood / 2; dR < neighbourhood / 2 + 1; dR++) {
							// check we are not outside the image
							if (accR + dR < 0 || accR + dR >= rDim)
								continue;
							int neighbourIdx = (accPhi + dPhi + 1) * (rDim + 2)
									+ accR + dR + 1;
							if (accumulator[idx] < accumulator[neighbourIdx]) {
								// the current idx is not a local maximum!
								bestCandidate = false;
								break;
							}
						}
						if (!bestCandidate)
							break;
					}
					if (bestCandidate) {
						// the current idx *is* a local maximum
						bestCandidates.add(idx);
					}
				}
			}

		}

		Collections.sort(bestCandidates, comparator);

		ArrayList<PVector> lines = new ArrayList<PVector>();

		/*
		 * PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
		 * 
		 * for (int i = 0; i < accumulator.length; i++) { houghImg.pixels[i] =
		 * color(min(255, accumulator[i])); }
		 * 
		 * houghImg.updatePixels(); houghImg.resize(350, 450); image(houghImg,
		 * 0, 0);
		 */

		// For each pair (r, ϕ) in the accumulator that received more that 200
		// votes, plot the corresponding line on top
		// of the image
		for (int idx = 0; idx < Math.min(bestCandidates.size(), nLines); idx++) {

			// first, compute back the (r, phi) polar coordinates:
			int accPhi = (int) (bestCandidates.get(idx) / (rDim + 2)) - 1;
			int accR = bestCandidates.get(idx) - (accPhi + 1) * (rDim + 2) - 1;
			float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
			float phi = accPhi * discretizationStepsPhi;

			lines.add(new PVector(r, phi));

			// Cartesian equation of a line: y = ax + b
			// in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
			// => y = 0 : x = r / cos(phi)
			// => x = 0 : y = r / sin(phi)
			// compute the intersection of this line with the 4 borders of
			// the image
			int x0 = 0;
			int y0 = (int) (r / sin(phi));
			int x1 = (int) (r / cos(phi));
			int y1 = 0;
			int x2 = edgeImg.width;
			int y2 = (int) (-cos(phi) / sin(phi) * x2 + r / sin(phi));
			int y3 = edgeImg.width;
			int x3 = (int) (-(y3 - r / sin(phi)) * (sin(phi) / cos(phi)));

			// Finally, plot the lines
			stroke(204, 102, 0);
			if (y0 > 0) {
				if (x1 > 0)
					line(x0, y0, x1, y1);
				else if (y2 > 0)
					line(x0, y0, x2, y2);
				else
					line(x0, y0, x3, y3);
			} else {
				if (x1 > 0) {
					if (y2 > 0)
						line(x1, y1, x2, y2);
					else
						line(x1, y1, x3, y3);
				} else
					line(x2, y2, x3, y3);
			}
		}

		return lines;
	}

}