package br.com.futbol.nxt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Detects the color in image and returns the center and the radius of the color
 * contour.
 * 
 * @author renatol
 * 
 */
public class ColorDetector {
	// Lower and Upper bounds for range checking in HSV color space
	private Scalar mLowerBound = new Scalar(0);
	private Scalar mUpperBound = new Scalar(0);
	// Minimum contour area in percent for contours filtering
	private static double mMinContourArea = 0.1;
	// Color radius for range checking in HSV color space
	private Scalar mColorRadius = new Scalar(25, 50, 50, 0);
	private int radius;
	private Point center;

	// Cache
	Mat mPyrDownMat = new Mat();
	Mat mHsvMat = new Mat();
	Mat mMask = new Mat();
	Mat mDilatedMask = new Mat();
	Mat mHierarchy = new Mat();

	public ColorDetector() {
	}

	public ColorDetector(Scalar hsvColor) {
		setHsvColor(hsvColor);
	}

	public void setHsvColor(Scalar hsvColor) {
		double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]
				- mColorRadius.val[0]
				: 0;
		double maxH = (hsvColor.val[0] + mColorRadius.val[0] <= 255) ? hsvColor.val[0]
				+ mColorRadius.val[0]
				: 255;

		mLowerBound.val[0] = minH;
		mUpperBound.val[0] = maxH;

		mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
		mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

		mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
		mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

		mLowerBound.val[3] = 0;
		mUpperBound.val[3] = 255;

	}

	public void setMinContourArea(double area) {
		mMinContourArea = area;
	}

	/**
	 * Search by color in image defined in <code> setHsvColor</code> and found
	 * center and radius contour region
	 * 
	 * @param rgbaImage
	 * @return if detected
	 */
	public boolean detect(Mat rgbaImage) {
		Imgproc.pyrDown(rgbaImage, mPyrDownMat);
		Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

		Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

		Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
		Imgproc.dilate(mMask, mDilatedMask, new Mat());
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(mDilatedMask, contours, mHierarchy,
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Find max contour area
		double maxArea = 0;
		Iterator<MatOfPoint> each = contours.iterator();
		while (each.hasNext()) {
			MatOfPoint wrapper = each.next();
			double area = Imgproc.contourArea(wrapper);
			if (area > maxArea)
				maxArea = area;
		}

		for (MatOfPoint contour : contours) {
			if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
				Core.multiply(contour, new Scalar(4, 4), contour);

				// http://docs.opencv.org/doc/tutorials/imgproc/shapedescriptors/bounding_rects_circles/bounding_rects_circles.html
				MatOfPoint2f contour2 = new MatOfPoint2f(contour.toArray());

				MatOfPoint2f contourPoly = new MatOfPoint2f();
				Imgproc.approxPolyDP(contour2, contourPoly, 3, true);
				float[] radius = new float[1];
				center = new Point();
				Imgproc.minEnclosingCircle(contourPoly, center, radius);
				this.radius = (int) radius[0];
				return true;// good why
			}
		}
		return false;
	}

	/**
	 * 
	 * @return the center of color contour
	 */
	public Point getCenter() {
		return center;
	}

	/**
	 * 
	 * @return size in radius to generate a circle
	 */
	public int getRadius() {
		return radius;
	}

}
