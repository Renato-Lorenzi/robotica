package br.com.futbol.nxt.robot;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import br.com.futbol.nxt.ColorDetector;

public class RobotController {
	private ColorDetector mBallDetector = new ColorDetector(new Scalar(
			2.609375, 174.5625, 255.0, 0.0));
	private ColorDetector mRobotDetector = new ColorDetector(new Scalar(0, 255,
			0));

	private RobotMove robotMove;

	public Mat go(Mat mRgba) {
		if (mRobotDetector.detect(mRgba) && mBallDetector.detect(mRgba)) {
			Point robotCenter = mRobotDetector.getCenter();
			Point ballCenter = mBallDetector.getCenter();

			if (!equalsPos(robotCenter.x, ballCenter.x)) {
				if (robotCenter.x < ballCenter.x) {
					robotMove.incX();
				} else {
					robotMove.decX();
				}
			} else if (!equalsPos(robotCenter.y, ballCenter.y)) {
				if (robotCenter.y < ballCenter.y) {
					robotMove.incY();
				} else {
					robotMove.decY();
				}
			}
		}
		return mRgba;
	}

	private boolean equalsPos(double a, double b) {
		return Math.abs(a - b) < 50;
	}
}
